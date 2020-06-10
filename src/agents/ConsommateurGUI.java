package agents;

import jade.core.Runtime;

import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import org.w3c.dom.css.CSS2Properties;

import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Article;

public class ConsommateurGUI extends Application{
	
	protected ConsommateurAgent consommateurAgent;
	protected ObservableList<String> observableList;
	protected Stage dialogDisplay;
	
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
		
		Button buttonNouveau = new Button("Nouveau");
		hBox.getChildren().addAll(buttonNouveau);
		hBox.setStyle("-fx-background-color: #336699;");
		hBox.setAlignment(Pos.BASELINE_RIGHT);
		
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
		
		buttonNouveau.setOnAction(evt -> {
			
			GridPane grid = new GridPane();
	    	grid.setAlignment(Pos.CENTER);
	    	grid.setHgap(10);
	    	grid.setVgap(10);
	    	grid.setPadding(new Insets(25, 25, 25, 25));
	    	
	    	Text scenetitle = new Text("Welcome");
	    	scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	    	grid.add(scenetitle, 0, 0, 2, 1);

	    	Label article = new Label("Article :");
	    	grid.add(article, 0, 1);

	    	TextField articleTextField = new TextField();
	    	grid.add(articleTextField, 1, 1);

	    	Label prixMax = new Label("Prix Max:");
	    	grid.add(prixMax, 0, 2);

	    	TextField prixMaxTextField = new TextField(){
	            @Override
	            public void replaceText(int start, int end, String text) {
	                if (!text.matches("[a-z, A-Z]")) {
	                    super.replaceText(start, end, text);                     
	                }
	                prixMax.setText("Prix Max:");
	            }
	 
	            @Override
	            public void replaceSelection(String text) {
	                if (!text.matches("[a-z, A-Z]")) {
	                    super.replaceSelection(text);
	                }
	            }
	        };
	    	grid.add(prixMaxTextField, 1, 2);
	    	
	    	Label qnte = new Label("Quantité:");
	    	grid.add(qnte, 0, 3);

	    	TextField qnteTextField = new TextField(){
	            @Override
	            public void replaceText(int start, int end, String text) {
	                if (!text.matches("[a-z, A-Z]")) {
	                    super.replaceText(start, end, text);                     
	                }
	                qnte.setText("Quantité:");
	            }
	 
	            @Override
	            public void replaceSelection(String text) {
	                if (!text.matches("[a-z, A-Z]")) {
	                    super.replaceSelection(text);
	                }
	            }
	        };
	    	grid.add(qnteTextField, 1, 3);
	    	
	    	//PasswordField pwBox = new PasswordField();
	    	//grid.add(pwBox, 1, 2);
	    	
	    	Button chercherbtn = new Button("Chercher");
	    	HBox hbBtn = new HBox(10);
	    	hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	    	hbBtn.getChildren().add(chercherbtn);
	    	grid.add(hbBtn, 1, 4);
	    	
	    	final Text actiontarget = new Text();
	        grid.add(actiontarget, 1, 6);
	        
	        Scene s = new Scene(grid,300,500);
	    	
	    	Stage dialog = new Stage();
	        
	        chercherbtn.setOnAction(evte -> {
	        	
	        	String articleC = articleTextField.getText();
	        	String prixMaxC = prixMaxTextField.getText();
	        	String qnteC = qnteTextField.getText();
	        	int prixMaxInt = 0;
	        	int qnteInt = 0;
	        	
	        	try {
	        		prixMaxInt = Integer.parseInt(prixMaxC);
	        	} catch(Exception e) {
	        		actiontarget.setFill(Color.FIREBRICK);
	        		actiontarget.setText("Le prix est entier");
	        	}
	        	
	        	try {
	        		qnteInt = Integer.parseInt(qnteC);
	        	} catch(Exception e) {
	        		actiontarget.setFill(Color.FIREBRICK);
	        		actiontarget.setText("La quantité est entier");
	        	}
	        	
	        	if( prixMaxInt > 0 && qnteInt > 0 && !articleC.isEmpty()) {
	        		//observableList.add(leLivre);
					GuiEvent event = new GuiEvent(this, 1);
					event.addParameter(articleC);
					event.addParameter(prixMaxInt);
					event.addParameter(qnteInt);
					consommateurAgent.onGuiEvent(event);
					dialog.close();
	        	}
	        });
	    	
	    				
	    	dialog.setTitle("Nouveau achat");
	    	dialog.setScene(s);

	    	dialog.initOwner(primaryStage);
	    	dialog.initModality(Modality.APPLICATION_MODAL); 
	    	dialog.showAndWait();
	    	
		});  
		
		dialogDisplay = new Stage();
		dialogDisplay.setTitle("Nouveau achat");

		dialogDisplay.initOwner(primaryStage);
		dialogDisplay.initModality(Modality.APPLICATION_MODAL); 
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(() -> {
			observableList.add(aclMessage.getContent() + ", " 
							+ aclMessage.getSender().getName());
		});
	}
	
	public void recevoirProposition(ACLMessage aclMessage) {
		Platform.runLater(() -> {
			TableView<Article> table = new TableView<Article>();
			
			Scene scene = new Scene(new Group());
			final Label label = new Label("Selectionnez un article");
	        label.setFont(new Font("Arial", 16));
	 
	        table.setEditable(true);
	 
	        TableColumn articleCol = new TableColumn("Article");
	        articleCol.setMinWidth(150);
	        articleCol.setCellValueFactory(
		            new PropertyValueFactory<Article,String>("nom")
		        );
	        
	        TableColumn vendeurCol = new TableColumn("Vendeur");
	        vendeurCol.setMinWidth(150);
	        vendeurCol.setCellValueFactory(
		            new PropertyValueFactory<Article,String>("vendeur")
		        );
	        
	        TableColumn qnteCol = new TableColumn("Qnte");
	        qnteCol.setMinWidth(30);
	        qnteCol.setCellValueFactory(
		            new PropertyValueFactory<Article,Integer>("qnte")
		        );
	        
	        TableColumn prixUCol = new TableColumn("Prix U.");
	        prixUCol.setMinWidth(40);
	        prixUCol.setCellValueFactory(
		            new PropertyValueFactory<Article,Integer>("prix")
		        );
	        TableColumn prixTCol = new TableColumn("Prix T.");
	        prixTCol.setMinWidth(50);
	        prixTCol.setCellValueFactory(
		            new PropertyValueFactory<Article,Integer>("total")
		        );
	        
	        //Création des données
	        final ObservableList<Article> data = FXCollections.observableArrayList();
	        String[] parts = aclMessage.getContent().split("//");
	        if(parts.length > 0) {
	        	for(int i = 0; i <= parts.length-1; i++) {
	        		String[] parts2 = parts[i].split("--");
	        		data.add( new Article(parts2[0], Integer.parseInt(parts2[1]), Integer.parseInt(parts2[2]), parts2[3]));
	        	}
	        }
	        
	        table.setItems(data);
	        table.getColumns().addAll(articleCol, vendeurCol, qnteCol, prixUCol, prixTCol);

	        
	        Button selectButton = new Button("Sellection");
	        //selectButton.applyCss(CSS2Propertie);
	        selectButton.setOnAction(evte -> {
	        	Article article = (Article) table.getSelectionModel().getSelectedItem();
	        	if(article != null) {
		        	System.out.println(article.getPrix());
		        	
		        	GuiEvent event = new GuiEvent(this, 2);
					event.addParameter(article.getNom());
					event.addParameter(article.getQnte());
					event.addParameter(article.getVendeur());
					consommateurAgent.onGuiEvent(event);
		        	dialogDisplay.close();
	        	}
	        	
	        });
	        HBox hb = new HBox();
	        hb.getChildren().addAll(selectButton);
	        hb.setSpacing(3);
	        hb.setAlignment(Pos.BASELINE_CENTER);
	 
	        final VBox vbox = new VBox();
	        vbox.setSpacing(5);
	        vbox.setPadding(new Insets(10, 0, 0, 10));
	        vbox.getChildren().addAll(label, table, hb);
	 
	        ((Group) scene.getRoot()).getChildren().addAll(vbox);
	 
	        dialogDisplay.setScene(scene);
			
			
			dialogDisplay.showAndWait();
		});
	}
}
