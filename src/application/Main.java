package application;


public class Main extends BaseApp{

    public static void main(String[] args){
        MainMenu.changeLanguage();

        MainMenu.getAuthMenu().start();

        if(isAuthenticated()) 
            MainMenu.getMainMenu().start();
        
        shutdown();
    }


}
