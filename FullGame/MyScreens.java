package FullGame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MyScreens extends AbstractAppState implements ScreenController {
	  private Nifty nifty;
	  private Screen screen;
	  private SimpleApplication app;
	 
	  /** custom methods */ 
	 
	  public MyScreens(HippoDisplay a) { 
	    /** Your custom constructor, can accept arguments */ 
		  this.app = a;
	  } 
	 
	  /** Nifty GUI ScreenControl methods */ 
	 
	  public void bind(Nifty nifty, Screen screen) {
	    this.nifty = nifty;
	    this.screen = screen;
	  }
	 
	  public void singlePlayerGame() { 
		  System.out.println("YEAH");
		  nifty.gotoScreen("hud");
	  }
	  
	 // public void multiplayerGame(){	  }
	 
	  /** jME3 AppState methods */ 
	 
	  @Override
	  public void initialize(AppStateManager stateManager, Application app) {
	    super.initialize(stateManager, app);
	    this.app=(SimpleApplication)app;
	  }
	  
	  public String getName(){
		  return System.getProperty("user.name");
	  }
	  
	  public void exitGame(){
		  	app.stop();
	  }
	 
	  
	  @Override
	  public void update(float tpf) { 
	  }

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	 

}
