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
	protected String article = "";
	protected int prixMax;
	protected int qnte;
	
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
				serviceDescription.setName("vente-articles");
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
												MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
												MessageTemplate.MatchPerformative(ACLMessage.REFUSE))))
								);
				ACLMessage aclMessage = receive(messageTemplate);
				if(aclMessage != null) {
					gui.logMessage(aclMessage);
					
					switch (aclMessage.getPerformative()) {
					case ACLMessage.REQUEST:
						String[] liste = aclMessage.getContent().split("---");
						article = liste[0];
						prixMax = Integer.parseInt(liste[1]);
						qnte = Integer.parseInt(liste[2]);
						
						ACLMessage reply = aclMessage.createReply();
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Je vais chercher " + article + " au prix unitaire Maximum de " + prixMax);
						send(reply);
						
						
						ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);
					
						aclMessage2.setContent(article + "---" + qnte);
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
							String retour = "";
							for(ACLMessage offre : replies) {
								if(offre.getPerformative() == ACLMessage.PROPOSE) {
									String[] parts = offre.getContent().split("--");
									
									if(parts.length == 4) {
										retour += offre.getContent() + "//";
									}
								}
							}
							
							ACLMessage aclMessage3 = new ACLMessage(ACLMessage.INFORM);
							aclMessage3.addReceiver(new AID("Consommateur", AID.ISLOCALNAME));
							aclMessage3.setContent(retour);
							send(aclMessage3);
							
							//ACLMessage aclMessageAccept = meilleureOffre.createReply();
							//aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							//aclMessageAccept.setContent("J'accepte ta proposition");
							//send(aclMessageAccept);
							compteur = 0;
							replies.clear();
						}
						
						break;
						
					case ACLMessage.REFUSE:
						
						++compteur;
						replies.add(aclMessage);
						//si tous les vendeurs on envoyés leur proposition
						if(compteur == vendeurs.length) {
							String retour = "";
							for(ACLMessage offre : replies) {
								if(offre.getPerformative() == ACLMessage.PROPOSE) {
									String[] parts = offre.getContent().split("--");
									
									if(parts.length == 4) {
										retour += offre.getContent() + "//";
									}
								}
							}
							
							ACLMessage aclMessage4 = new ACLMessage(ACLMessage.INFORM);
							aclMessage4.addReceiver(new AID("Consommateur", AID.ISLOCALNAME));
							aclMessage4.setContent(retour);
							send(aclMessage4);
							
							//ACLMessage aclMessageAccept = meilleureOffre.createReply();
							//aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							//aclMessageAccept.setContent("J'accepte ta proposition");
							//send(aclMessageAccept);
							compteur = 0;
							replies.clear();
						}
						
						break;
						
					case ACLMessage.AGREE:
						
						ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CONFIRM);
						aclMessage3.addReceiver(new AID("Consommateur", AID.ISLOCALNAME));
						aclMessage3.setContent(aclMessage.getContent());
						aclMessage3.setContent("La meilleure proposition pour le livre " +
								article + " est " + minimum);
						send(aclMessage3);
						
						break;
					
					case ACLMessage.CONFIRM:
						
						if(aclMessage.getSender().getLocalName().equals("Consommateur")) {
							String v = aclMessage.getContent().split("---")[2];
							Boolean trouveV = false;
							
							for(AID aid : vendeurs) {
								if(aid.getName().equals(v)) {
									aclMessage2 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
									aclMessage2.addReceiver(aid);
									aclMessage2.setContent(aclMessage.getContent().split("---")[0] + "---" + aclMessage.getContent().split("---")[1]);
									send(aclMessage2);
									trouveV = true;
									break;
								}
							}
							
							if(!trouveV) {
								reply = aclMessage.createReply();
								reply.setPerformative(ACLMessage.FAILURE);
								reply.setContent(aclMessage.getContent().split("---")[0]);
								send(reply);
							}
						} else if(estVendeur(aclMessage.getSender())) {
							
						} else {
							
						}
						
						break;
						
					case ACLMessage.FAILURE:
						
						reply = aclMessage.createReply();
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent(aclMessage.getContent().split("---")[0]);
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
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	protected Boolean estVendeur(AID aid) {
		for(AID ai : vendeurs) {
			if(ai.getName().equals(aid.getName())) {
				return true;
			}
		}
		return false;
	}

}
