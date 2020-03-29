package agents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.df;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AcheteurAgent extends GuiAgent {
	
	protected AcheteurGui gui;
	protected AID[] vendeurs;
	protected double minimum = 0.0;
	protected String livre = "";
	
	@Override
	protected void setup() {
		if(getArguments().length == 1) {
			gui = (AcheteurGui) getArguments()[0];
			gui.acheteurAgent = this;
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		//On recupère la liste des vendeur qui offre le service de vente de livre toutes les 5 secondes
		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 5000) {
			
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				DFAgentDescription dfAgentDescription = new DFAgentDescription();
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("vente-livres");
				dfAgentDescription.addServices(serviceDescription);
				try {
					DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
					
					vendeurs = new AID[results.length];
					for(int i = 0; i < vendeurs.length; i++) {
						vendeurs[i] = results[i].getName();
					}
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			//Pour compter me le nombre de vendeur qui envoye leur proposition
			private int compteur = 0;
			private List<ACLMessage> replies = new ArrayList<ACLMessage>();
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
				//si on veut que le smessage ne soit qu'un request ou propose ou agree ou refuse
				MessageTemplate messageTemplate = 
						MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
								MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
										MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
												MessageTemplate.MatchPerformative(ACLMessage.REFUSE)))
								);
				ACLMessage aclMessage = receive(messageTemplate);
				if(aclMessage != null) {
					gui.logMessage(aclMessage);
					
					switch (aclMessage.getPerformative()) {
					case ACLMessage.REQUEST:
						livre = aclMessage.getContent();
						
						ACLMessage reply = aclMessage.createReply();
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Je vais acheter le livre : " + livre);
						send(reply);
						
						
						ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);
					
						aclMessage2.setContent("Je veux le prix du livre : " + livre);
						for(AID aid : vendeurs) {
							aclMessage2.addReceiver(aid);
						}
						
						send(aclMessage2);
						
						break;
					case ACLMessage.PROPOSE:
						
						++compteur;
						replies.add(aclMessage);
						//si tous les vendeurs on envoyés leur proposition
						if(compteur == vendeurs.length) {
							ACLMessage meilleureOffre = replies.get(0);
							minimum = Double.parseDouble(replies.get(0).getContent());
							for(ACLMessage offre : replies) {
								double price = Double.parseDouble(offre.getContent());
								if(price < minimum) {
									minimum = price;
									meilleureOffre = offre;
								}
							}
							
							ACLMessage aclMessageAccept = meilleureOffre.createReply();
							aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							aclMessageAccept.setContent("J'accepte ta proposition");
							send(aclMessageAccept);
						}
						
						break;
						
					case ACLMessage.AGREE:
						
						ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CONFIRM);
						aclMessage3.addReceiver(new AID("Consommateur", AID.ISLOCALNAME));
						aclMessage3.setContent(aclMessage.getContent());
						aclMessage3.setContent("La meilleure proposition pour le livre " +
								livre + " est " + minimum);
						send(aclMessage3);
						
						break;
					case ACLMessage.REFUSE:
						
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
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
