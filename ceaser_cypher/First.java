import java.util.Scanner;

public class First {
    public static String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                char shifted = (char) ((character - base + shift) % 26 + base);
                result.append(shifted);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static String decrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                char shifted = (char) ((character - base - shift + 26) % 26 + base);
                result.append(shifted);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner Scan = new Scanner(System.in);

        System.out.print("Enter the message: ");
        String message = Scan.nextLine();

        
        
        System.out.print("Enter the shift (0-25): ");
        int shift = Scan.nextInt();

        Scan.nextLine();
        
        boolean trueChoice = false;
        while (!trueChoice) {
            System.out.println("Enter either to encrypt or decrypt: ");
            System.out.println("a. Encrypt \n b. Decrypt");
            System.out.print("Choose ['a', 'b']: ");
            String choice = Scan.nextLine();

            if (choice.equals("a")) {
                if (shift == 0) {
                    System.out.println(message);

                } else {
                    String res = encrypt(message, shift);
                    System.out.println(res);
                }
                trueChoice = true;
            } else if (choice.equals("b")) {
                if (shift == 0) {
                    System.out.println(message);

                } else {
                    String res = decrypt(message, shift);
                    System.out.println(res);
                }
                trueChoice = true;
            } else {
                System.out.println("Invalid choice!! Please choose either to encrypt or decrypt.");
            }
        }
        Scan.close();
    }

}
