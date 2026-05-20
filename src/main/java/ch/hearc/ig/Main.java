package ch.hearc.ig;
import ch.hearc.ig.apiService.Connexion;
import ch.hearc.ig.apiService.ConnexionManager;
import ch.hearc.ig.apiService.FlowManager;
import ch.hearc.ig.business.Receipt;
import org.w3c.dom.ls.LSOutput;

import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean valid = true;
        Connexion connexion = new Connexion();
        ConnexionManager connexionManager = new ConnexionManager();
        FlowManager flowManager = new FlowManager();
        Map<Integer, Receipt> quittances = new HashMap<>();




    do {
        System.out.println("""
        =================================
        LOGIN
        =================================
        """ );

        System.out.print("USERNAME : ");
        String username = scanner.nextLine();

        System.out.print("PASSWORD : ");
        String password = scanner.nextLine();
        try {
            connexion = connexionManager.login(username, password);
            valid = true;
            System.out.println("LOGIN SUCCESSFULLY");
        }catch(Exception e) {
            System.out.println("Invalid username or password");
            valid = false;
        }
    }while (!valid);

    quittances = flowManager.getValidatedReceipt(connexion);

    if (flowManager.integrate(quittances, connexion)){
        System.out.println(quittances.size() + " Receipts have been integrated");
    }
    System.out.println("The following receipts have been integrated :");
    for (Integer i : quittances.keySet()) System.out.println(quittances.get(i));
    }
}
