/**Copyright 2015 Zhaorui Chen, Joshua Campbell

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 **/

package com.example.zhaorui.zhaorui_reflex.Model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

public class BuzzerRecordManager extends RecordManager{
    /*
     * This class is the class inherit from RecordManager
     * intended to save and load statistics obtained from the
     * GameBuzzer, and to send emails based on the stats
     * The class is used in BuzzerActivity,BuzzerStat4pActivity,
     * BuzzerStat3pActivity and BuzzerStat2pActivity
     *
     * Two methods below are modified based on Joshua Campbell's lonelyTwitter Program, 2015
     */

    private int numPlayers;
    private Player[] playersArray;

    public BuzzerRecordManager(Context context, String fileName, int numPlayers) {
        super(context, fileName);
        this.numPlayers = numPlayers;
    }

    // the following method is modified based on Joshua Campbell's lonelyTwitter, 2015
    public Player[] loadBuzzerFromFile(){
        try {

            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            // Following line based on https://google.gson.googlecode.com/svn
            Type listType = new TypeToken<Player[]>() {}.getType();
            playersArray = gson.fromJson(in, listType);
            return playersArray;

        } catch (FileNotFoundException e) {

            playersArray = new Player[numPlayers];
            for (int i=0;i<numPlayers;i++){
                playersArray[i] = new Player(i+1);
            }
            return playersArray;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // the following method is modified based on Joshua Campbell's lonelyTwitter, 2015
    public void saveBuzzerInFile(Player[] playersArray) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);//
            Gson gson = new Gson();
            gson.toJson(playersArray, writer); //save data
            writer.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearData(int numPlayers){
        playersArray = new Player[numPlayers];
        for(int i=0;i<numPlayers;i++) {
            playersArray[i] = new Player(i+1);
        }
        this.saveBuzzerInFile(playersArray);
    }

    // Following function based on http://blog.csdn.net/way_ping_li/article/details/9038655, 2015
    public void sendEmail(String reciever){
        Player[] playersArray = this.loadBuzzerFromFile();
        Intent stats = new Intent(Intent.ACTION_SENDTO);
        stats.setData(Uri.parse("mailto:" + reciever));
        stats.putExtra(Intent.EXTRA_SUBJECT, "My Game Buzzer Statistics");
        String content = new String();
        for(int i=0;i<this.numPlayers;i++){
            content +=String.format("Player%d buzzes: %d\n", i+1, this.playersArray[i].getWinningTimes());
        }
        content +="\nZhaorui Chen";
        stats.putExtra(Intent.EXTRA_TEXT, content);

        context.startActivity(stats);
    }
}
