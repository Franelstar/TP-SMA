package containers;

import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import agents.ConsommateurAgent;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsommateurComtainer extends Application{
	
	protected ConsommateurAgent consommateurAgent;
	protected ObservableList<String> observableList;
	
	public static void main(String[] args) {
		launch(args); // Pour que l'interface graphique démarre
	}
	
	public void startContainer() throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer container = runtime.createAgentContainer(profileImpl);
		
		//On crée un agent de type consommateur dans ce controller
		AgentController agentController = container
				.createNewAgent("Consommateur", "agents.ConsommateurAgent", new Object[] {this});
		
		//On démarre l'agent
		agentController.start();
		
		//Plus besoin
		//container.start();
	}

	public void setConsommateurAgent(ConsommateurAgent consommateurAgent) {
		this.consommateurAgent = consommateurAgent;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		startContainer();
		
		primaryStage.setTitle("Consommateur");
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(20));
		hBox.setSpacing(15);
		Label label = new Label("Livre :");
		TextField livre = new TextField();
		Button buttonAcheter = new Button("Acheter");
		hBox.getChildren().addAll(label, livre, buttonAcheter);
		
		VBox vBox = new VBox();
		observableList = FXCollections.observableArrayList();
		vBox.setPadding(new Insets(10));
		ListView<String> listViewMessages = new ListView<String>(observableList);
		vBox.getChildren().add(listViewMessages);
		
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(hBox);
		borderPane.setCenter(vBox);
		
		Scene scene = new Scene(borderPane, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonAcheter.setOnAction(evt -> {
			String leLivre = livre.getText();
			//observableList.add(leLivre);
			GuiEvent event = new GuiEvent(this, 1);
			event.addParameter(leLivre);
			consommateurAgent.onGuiEvent(event);
		});
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(() -> {
			observableList.add(aclMessage.getContent());
		});
	}
} 
