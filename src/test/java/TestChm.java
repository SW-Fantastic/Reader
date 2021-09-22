import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.jchmlib.ChmFile;
import org.swdc.reader.core.locators.ChmBookLocator;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class TestChm {


    public static void main(String[] args) {

        int a[][] = new int[5][5];
        int i,j,k = 1;

        for (i = 0; i < 5; i ++) {
            for (j =0 ; j < 5; j++){
                if ((i + j) < 5) {
                    a[i][j] = k;
                    k++;
                    if (k > 9) {
                        k = 1;
                    }
                } else {
                    a[i][j] = 0;
                }
            }
        }

        for (i = 0; i < 5; i ++) {
            for (j = 0; j < 5; j++) {
                System.out.print(a[i][j]);
            }
            System.out.println("\n");
        }
    }


}
