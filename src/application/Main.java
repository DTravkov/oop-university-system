package application;


public class Main extends BaseApp{

    public static void main(String[] args){
        Menus.getAuthMenu().start();
        if(isAuthenticated()) 
            Menus.getMainMenu().start();
        shutdown();
    }

}
