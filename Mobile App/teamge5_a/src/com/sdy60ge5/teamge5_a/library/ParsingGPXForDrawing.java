package com.sdy60ge5.teamge5_a.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.graphics.Color;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/********************************************************************************
 * Κλάση που αναλύει ένα αρχείο GPX και σχεδιάζει τα στοιχεία του σε έναν χάρτη *
 * ******************************************************************************/
public class ParsingGPXForDrawing {
    private File mFile;
    private GoogleMap mGoogleMap;

    public ParsingGPXForDrawing(File File,GoogleMap GoogleMap){
        this.mFile = File;
        this.mGoogleMap = GoogleMap;
    }


    public void decodeGpxForWpts(){

        //Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try{

            //Creates a new instance of a DocumentBuilder using the currently configured parameters.
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(mFile);
            //Parse the content of the given InputStream as an XML document and return a new DOM Document object.
            //Conceptually, it is the root of the document tree, and provides the primary access to the document's data.
            Document document = documentBuilder.parse(fileInputStream);
            //--------------------------------------------------------------------
            fileInputStream.close();
            //--------------------------------------------------------------------
            //This is a convenience attribute that allows direct access to the child node that is the document element of the document.
            Element elementRoot = document.getDocumentElement();
            NodeList nodeListOfWpt = elementRoot.getElementsByTagName("wpt");//Η λίστα των κόμβων με όλα τα wpt
            for(int i = 0; i < nodeListOfWpt.getLength(); i++){
                Node node = nodeListOfWpt.item(i);
                //-------------------------------------------------------
                Element waypointElement=(Element)node;//Το element του wpt που βρισκόμαστε
                NodeList nodelist_name = waypointElement.getElementsByTagName("name");//Η λίστα των name του waypoint που βρισκόμαστε
                String name = nodelist_name.item(0).getTextContent();//Ουσιαστικά υπάρχει μόνο ένα name, άρα παίρνουμε το πρώτο της λίστας (index 0)
                //------------------------------------------------------


                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                addWayPointInMap(new Waypoint(newLatitude_double,newLongitude_double,name),mGoogleMap);
            }



        }catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

    public void decodeGPXForTrksegs(){

        //Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            //Creates a new instance of a DocumentBuilder using the currently configured parameters.
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(mFile);
            //Parse the content of the given InputStream as an XML document and return a new DOM Document object.
            //Conceptually, it is the root of the document tree, and provides the primary access to the document's data.
            Document document = documentBuilder.parse(fileInputStream);

            //------------------------------------------------------------------------------------------------------
            fileInputStream.close();
            //------------------------------------------------------------------------------------------------------

            //This is a convenience attribute that allows direct access to the child node that is the document element of the document.
            Element elementRoot = document.getDocumentElement();

            NodeList nodeListOfTrseg = elementRoot.getElementsByTagName("trkseg");//Η λίστα των κόμβων με όλα τα trkseg

            for(int i=0;i<nodeListOfTrseg.getLength();i++){//Για όλα τα trkseg

                Element trksegElement = (Element)nodeListOfTrseg.item(i);//To element του iοστου trkseg
                NodeList nodeListOftrkpt = trksegElement.getElementsByTagName("trkpt");//Η λίστα των trkpt του trkseg που βρισκόμαστε

                ArrayList<LatLng> singleList = new ArrayList<LatLng>();   //Έτσι ώστε να αδειάζει η λίστα από τα προηγούμενα στοιχεία
                for(int j=0;j<nodeListOftrkpt.getLength();j++){//Για όλα τα trkpt του trkseg που βρισκόμαστε
                    Node nodeOftrkpt = nodeListOftrkpt.item(j);//Ο κόμβος του trkpt που βρισκόμαστε

                    NamedNodeMap attributes = nodeOftrkpt.getAttributes();//Τα attributes του trkpt που βρισκόμαστε

                    String newLatitude = attributes.getNamedItem("lat").getTextContent();
                    Double newLatitude_double = Double.parseDouble(newLatitude);

                    String newLongitude = attributes.getNamedItem("lon").getTextContent();
                    Double newLongitude_double = Double.parseDouble(newLongitude);

                    LatLng newLocation = new LatLng(newLatitude_double,newLongitude_double);

                    singleList.add(newLocation);


                }
                addPolylineInMap(singleList,mGoogleMap);
                //--------------------------
                singleList=null;
                //-----------------------
            }

            fileInputStream.close();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

    private void addPolylineInMap(ArrayList<LatLng> trkseg,GoogleMap GoogleMap){
        if (GoogleMap != null){
            PolylineOptions rectOptions = new PolylineOptions();
            rectOptions.width(5)
                    .color(Color.RED)
                    .geodesic(true);
            for(int i = 0; i < trkseg.size(); i++){
                rectOptions.add(new LatLng(trkseg.get(i).latitude, trkseg.get(i).longitude));
            }
            GoogleMap.addPolyline(rectOptions);
        }
        //---------------------------
        trkseg=null;
        //----------------------
    }



    private void addWayPointInMap(Waypoint wpt,GoogleMap Map){
        if (Map != null){
            // create marker
            MarkerOptions marker = new MarkerOptions().position(new LatLng(wpt.getLat(), wpt.getLon())).title(wpt.getName());
            // ROSE color icon
            // marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            // adding marker
            Map.addMarker(marker);
        }
        //----------------------------
        wpt=null;
        //----------------------------
    }
}

