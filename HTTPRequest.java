
/******************************************************************************
 * HTTPRequest command-line feature.
 ******************************************************************************/

public class HTTPRequest {

    public static void retrieveAndPrint(String url) {

        URLReader request = new URLReader(url);

        if (request.readURL()) {

            System.out.println(request.toString());

        } else {

            System.out.println("Unable to retrieve URL: " + url);
        }
    }
}
