package com.example.android.unicarsindos.utilities;


import java.util.Arrays;

public class StringUtils {


    public static String[] seperateName(String name){
        char[]nameArray=name.toCharArray();
        String fName;
        String sName;
        int index=0;
        for(char ch: nameArray){
            if(ch==' '){
                break;
            }
            index++;
        }
        fName=name.substring(0,index);
        sName=name.substring(index+1,name.length());
        return new String[]{fName,sName};
    }


    public static String upperCaseFirstLetter(String word){
        StringBuilder builder= new StringBuilder(word);
        builder.setCharAt(0,Character.toUpperCase(builder.charAt(0)));
        return builder.toString();
    }

    public static String[] seperateAddress(String address){
        char[] addressArray=address.toCharArray();
        String mAddress;
        String mZipcode;
        int index=0;
        for(char ch: addressArray){
            if(ch==','){
                break;
            }
            index++;
        }
        mAddress=address.substring(0,index);
        mZipcode=address.substring(index+1,address.length());
        return new String[]{mAddress,mZipcode};
    }

    public static String[] seperateDays(String day,String aString){
        String[] daysHours = aString.split("\n");
        for(int i=0; i<daysHours.length; i++){
            Boolean found = Arrays.asList(daysHours[i].split(":")).contains(day);
            if(found){
                String[] temp=daysHours[i].split(":");
                String[] returnedString= temp[1].split("-");
                return returnedString;
            }
        }
        return null;
    }

}
