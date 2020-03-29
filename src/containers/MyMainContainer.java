package containers;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;

public class MyMainContainer {
	public static void main(String[] args) throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl(); //Pour configurer le container
		
		profileImpl.setParameter(ProfileImpl.GUI, "true");
		AgentContainer mainContainer = runtime.createMainContainer(profileImpl);
		mainContainer.start();
	}
}
