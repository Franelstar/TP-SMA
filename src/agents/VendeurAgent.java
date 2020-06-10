package agents;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import GUI.VendeurGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import model.Article;

public class VendeurAgent extends GuiAgent {
	
	protected VendeurGui gui;
	protected Article liste_articles[];
	List<String> givenList = Arrays.asList("Livre", "Ordinateur", "Téléphone", "Montre", "Table", "Stylo");
	
	@Override
	protected void setup() {
		if(getArguments().length == 1) {
			gui = (VendeurGui) getArguments()[0];
			gui.vendeurAgent = this;
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
				//On crée les articles
				Collections.shuffle(givenList);
				Random rnd = new Random();
				liste_articles = new Article[5];
				for(int i = 0; i <= 4; i++) {
					int prix = 1000 + rnd.nextInt(1000);
					int qnte = rnd.nextInt(10);
					String nom = givenList.get(i);
					liste_articles[i] = new Article(nom, prix, qnte, this.getAgent().getName());
				}
				
				DFAgentDescription agentDescription = new DFAgentDescription();
				agentDescription.setName(getAID());
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("Vente-articles");
				agentDescription.addServices(serviceDescription);
				try {
					DFService.register(myAgent, agentDescription);
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					gui.logMessage(aclMessage);
					switch (aclMessage.getPerformative()) {
						case ACLMessage.CFP:
							
							ACLMessage reply = aclMessage.createReply();
							//-------------------------------------------------------------
							//On vérifie les informations sur l'article cherché
							Boolean trouve = false;
							Article art = null;
							String[] liste = aclMessage.getContent().split("---");
							String article = liste[0];
							int qnte = Integer.parseInt(liste[1]);
							for(int i = 0; i <= liste_articles.length-1; i++) {
								if(liste_articles[i].getNom().toLowerCase().contains(article.toLowerCase())
										&& liste_articles[i].getQnte() >= qnte) {
									art = liste_articles[i];
									trouve = true;
									break;
								}
							}
							
							//-------------------------------------------------------------
							
							if(trouve && art.getQnte() > 0) {
								reply.setPerformative(ACLMessage.PROPOSE);
								reply.setContent(art.getNom() + "--" + String.valueOf(art.getPrix()) + "--" + String.valueOf(qnte) + "--" + art.getVendeur());
							} else {
								reply.setPerformative(ACLMessage.REFUSE);
							}
							
							send(reply);
							
							break;
						case ACLMessage.ACCEPT_PROPOSAL:
							reply = aclMessage.createReply();
							trouve = false;
							art = null;
							liste = aclMessage.getContent().split("---");
							article = liste[0];
							qnte = Integer.parseInt(liste[1]);
							
							for(int i = 0; i <= liste_articles.length-1; i++) {
								if(liste_articles[i].getNom().toLowerCase().contains(article.toLowerCase())
										&& liste_articles[i].getQnte() >= qnte) {
									art = liste_articles[i];
									trouve = true;
									break;
								}
							}
							
							if(trouve && art.getQnte() > 0) {
								art.setQnte(art.getQnte() - qnte);
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent(art.getNom() + "--" + String.valueOf(art.getPrix()) + "--" + String.valueOf(qnte) + "--" + art.getVendeur());
							} else {
								reply.setPerformative(ACLMessage.DISCONFIRM);
							}
							
							send(reply);
							
							break;
						default:
							break;
					}
				} else {
					block();
				}
			}
		});
	}

	@Override
	protected void onGuiEvent(GuiEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
