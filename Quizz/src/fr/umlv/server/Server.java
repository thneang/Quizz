package fr.umlv.server;

import fr.umlv.evaluation.EvaluationWord;
import fr.umlv.exercices.ExercisesManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends AbstractVerticle {


	private final Object monitor = new Object();
	private final int port = 8989;
	private final ExercisesManager exercicesManager;

	private final ExecutorService executor = Executors.newFixedThreadPool(4);

	/**
	 * Factory, create a server
	 * @param dir The directory of the exercise
	 * @return The server
	 */
	public static Server factoryServeur(File dir) {
		return new Server(ExercisesManager.createExercisesManager(dir));
	}

	private Server(ExercisesManager exercicesManager) {
		this.exercicesManager = exercicesManager;
	}

	
	private void initRequest() {
		Router router = Router.router(super.vertx);

		router.get("/exercises/").handler(this::getAllExercise);
		router.get("/closeexercise/:id").handler(this::closeExercise);
		router.get("/exercise/:id").handler(this::getExerciseRequest);
		BridgeOptions opts = new BridgeOptions().addInboundPermitted(
		new PermittedOptions().setAddress("client-server"))
		.addOutboundPermitted(new PermittedOptions().setAddress("server-client"));

		// Create the event bus bridge and add it to the router.
		SockJSHandler ebHandler = SockJSHandler.create(super.vertx).bridge(opts);
		router.route("/eventbus/*").handler(ebHandler);
		router.route("/api/reponse*").handler(BodyHandler.create());
		router.post("/api/reponse").blockingHandler(this::getEvaluation);

		this.vertx.createHttpServer().requestHandler(router::accept).listen(this.port);

		router.route().handler(StaticHandler.create());

	}

	private void checkForUpdate() {

		super.vertx.eventBus().consumer("server-update", message -> {
			try {
				publishUpdate(message.body());

			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		});
	}


	private void publishUpdate(Object message) throws IOException {
		JsonObject json = this.exercicesManager.treatMessage(message);
		if (json.containsKey("empty")) return;
		this.vertx.eventBus().publish("server-client", json.toString());
	}

	private static boolean isLocalMachine(RoutingContext routingContext)
			throws UnknownHostException {

		return (routingContext.request().localAddress().host()
				.equals(InetAddress.getLocalHost().getHostAddress()))
				|| (routingContext.request().localAddress().host()
						.equals("127.0.0.1"));
	}


	private void getAllExercise(RoutingContext routingContext) {
		this.executor.execute(() -> {
					synchronized (this.monitor) {
						try {
							if (!isLocalMachine(routingContext)) {
								return;
							}
						} catch (UnknownHostException e) {
							throw new IllegalStateException("local Host Error");
						}
						String result = this.exercicesManager.getMessageListExercice(this.port);
						routingContext.response().putHeader("content-type", "text/html").end(result);
					}
				});
	}

	/**
	 * See vertx doc
	 */
	@Override
	public void start() throws UnknownHostException {

		initRequest();
		checkForUpdate();

		System.out.println("http://localhost:" + this.port);

	}

	/**
	 * Get the evaluation of the word.
	 * @param routingContext The context of the request
	 */
	public void getEvaluation(RoutingContext routingContext) {

		this.executor.execute(() -> {
		

				JsonObject json = routingContext.getBodyAsJson();
				
				String word = json.getString("word");
				String translateWord = json.getString("translateWord");
				try{
					json.put("answer", EvaluationWord.getEval(word,translateWord));
				}catch(Exception e){
					e.printStackTrace();
				}

				routingContext.response().putHeader("content-type", "text/html").end(json.encode());

			
		});
	}

	/**
	 * Give the exercise to the request
	 * @param routingContext The context
	 */
	public void getExerciseRequest(RoutingContext routingContext) {

		this.executor.execute(() -> {
					synchronized (this.monitor) {
						// we retrieve the id of exercice
						String idExercice = routingContext.request().getParam("id");
						String stringExercise;
						try {
							stringExercise = this.exercicesManager.getMessageExercice(idExercice);
						} catch (IOException e) {

							throw new IllegalArgumentException("No such File directory");
						}
				routingContext.response().putHeader("content-type", "text/html").end(stringExercise);
			}
		});
	}

	/**
	 * Unload exercise if there are not in a window
	 * @param routingContext the context
	 */
	public void closeExercise(RoutingContext routingContext) {

		this.executor.execute(() -> {

			synchronized (this.monitor) {
				// we retrieve the id of exercise
				String idExercice = routingContext.request().getParam("id");
				this.exercicesManager.closeExerciceOnWindow(idExercice);
				routingContext.response().putHeader("content-type", "text/html").end("");
			}
		});
	}


}
