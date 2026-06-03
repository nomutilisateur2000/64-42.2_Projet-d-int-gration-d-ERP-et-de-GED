package ch.hearc.ig;

import ch.hearc.ig.apiService.Connexion;
import ch.hearc.ig.apiService.ConnexionManager;
import ch.hearc.ig.apiService.FlowManager;
import ch.hearc.ig.business.AttachementFile;
import ch.hearc.ig.business.Receipt;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
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
                """);

            System.out.print("USERNAME : ");
            String username = scanner.nextLine();

            System.out.print("PASSWORD : ");
            String password = scanner.nextLine();

            try {
                connexion = connexionManager.login(username, password);
                valid = true;
                System.out.println("LOGIN SUCCESSFULLY");
            } catch (Exception e) {
                System.out.println("Invalid username or password");
                valid = false;
            }
        } while (!valid);

        quittances = flowManager.getValidatedReceipts(connexion);

        if (flowManager.integrate(quittances, connexion)) {
            System.out.println(quittances.size() + " Receipts have been integrated");

            for (Integer i : quittances.keySet()) {
                Receipt receipt = quittances.get(i);
                try {
                    receipt = flowManager.getAttachment(connexion, receipt);
                    AttachementFile attachementFile = receipt.getFile();

                    if (attachementFile == null || attachementFile.getFile() == null) {
                        System.out.println("No attachment for receipt " + i);
                        continue;
                    }

                    byte[] fileBytes = Base64.getDecoder().decode(attachementFile.getFile());

                    String downloadsPath = System.getProperty("user.home") + "/Downloads/" + attachementFile.getName();
                    File destination = new java.io.File(downloadsPath);

                    Files.write(destination.toPath(), fileBytes);
                    System.out.println("Attachment downloaded : " + destination.getAbsolutePath());

                } catch (Exception e) {
                    System.out.println("Failed to download attachment for receipt " + i + " : " + e.getMessage());
                }
            }
        }

        System.out.println("The following receipts have been integrated :");
        for (Integer i : quittances.keySet()) System.out.println(quittances.get(i));
    }
}