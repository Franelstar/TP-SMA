package agents;

import containers.ConsommateurComtainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsommateurAgent extends GuiAgent {
	
	private transient ConsommateurComtainer gui; //Interface graphique
	
	/**
	 * 
	 * Agent qui est exécuté une fois que l'agent est instancié et déployé
	 * C'est la première méthode que l'agent exécute
	 */
	@Override
	protected void setup() {
		if(getArguments().length >=  1) {
			gui = (ConsommateurComtainer) getArguments()[0];
			gui.setConsommateurAgent(this);
		}
		
		//On crée des comportements
		
		/*
		 * Si on veut exécuter plusieurs comportements en parallèle, on utilise parallelBehaviour
		 */
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		// Est appelé une seule fois
		addBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				System.out.println("Action ..................");
			}
		});
		
		// Est appellé tout le temps (s'exécute à l'infini)
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					System.out.println("----------------------------");
					System.out.println("Reception du message");
					System.out.println(aclMessage.getContent());
					System.out.println(aclMessage.getSender().getName());
					System.out.println(aclMessage.getPerformative());
					System.out.println(aclMessage.getLanguage());
					System.out.println(aclMessage.getOntology());
					System.out.println("------------------------------");
					
					switch (aclMessage.getPerformative()) {
					case ACLMessage.CONFIRM:
						gui.logMessage(aclMessage);
						break;

					default:
						break;
					}
					
				}
				else
					block();
			}
		});
		
		// Appeler tous les x secondes	
		addBehaviour(new TickerBehaviour(this, 1000) {
			
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				//System.out.println("Je suis fait tout les 1 secondes dans l'agent " + myAgent.getAID().getName());
			}
		});
		
		/*Comportement générique
		 * 
		 * addBehaviour(new Behaviour() {
			private int compteur = 0;
			
			@Override
			public boolean done() {
				if(compteur == 10) return true
				else return false;
			}
			
			@Override
			public void action() {
				System.out.println("Etape " + compteur);
				compteur++;
			}
		});*/
	}
	
	/**
	 * 
	 * Fonction exécutée lorsqu'on demande à l'agent de migrer vers un autre container
	 * Elle est exécutée avant la migration
	 */
	@Override
	protected void beforeMove() {
		// TODO Auto-generated method stub
		super.beforeMove();
	}
	
	/**
	 * 
	 * Fonction exécutée lorsqu'on demande à l'agent de migrer vers un autre container
	 * Elle est exécutée après la migration
	 */
	@Override
	protected void afterMove() {
		// TODO Auto-generated method stub
		super.afterMove();
	}
	
	/**
	 * 
	 * Méthode appelé juste avant que l'agent ne meurt
	 */
	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}

	/**
	 * 
	 * A utilser lorsqu'on a un agent qui utilise une interface graphique
	 */
	@Override
	public void onGuiEvent(GuiEvent params) {
		// TODO Auto-generated method stub
		if(params.getType() == 1) {
			String livre = params.getParameter(0).toString();
			ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
			aclMessage.setContent(livre);
			aclMessage.addReceiver(new AID("ACHETEUR", AID.ISLOCALNAME));
			send(aclMessage);
		}
	}
}
