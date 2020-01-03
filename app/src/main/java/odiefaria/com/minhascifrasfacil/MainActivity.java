package odiefaria.com.minhascifrasfacil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private TextView txtLer;
    private String chord;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //manter a tela ligada
        setContentView(R.layout.activity_main);

        txtLer = (TextView) findViewById(R.id.edittext);
        txtLer.setKeyListener(null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                txtLer.setText(readTextFromUri(uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void carregarCifra(View view){
        Intent intent = new Intent();
        // Mostrar apenas txt
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Sempre mostre o seletor (se houver várias opções disponíveis)
        startActivityForResult(Intent.createChooser(intent, "Selecione a cifra - formato txt"), PICK_IMAGE_REQUEST);

    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String str;
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.getProperty("line.separator"));
            }
        }
        str = stringBuilder.toString();
        //efetua algumas correções para evitar que haja erros na transposição
        str = str.replaceAll("\t","   "); //substitui tabs no texto, por 3 espaços
        str = str.replaceAll("\\(", " \\(");
        str = str.replaceAll("\\)", "\\) ");
        str = str.replaceAll("\\]","\\] ");
        return str;
    }

    public void menosTom(View view){
        geraNovaCifra(-2);
    }

    public void menosSemitom(View view){
        geraNovaCifra(-1);
    }

    public void maisSemitom(View view){
        geraNovaCifra(1);
    }

    public void maisTom(View view){
        geraNovaCifra(2);
    }

    private void geraNovaCifra(int indiceTranspose){
        StringBuilder novoTexto = new StringBuilder();
        String str = txtLer.getText().toString();
        String linhas[] = str.split("\\R");
        for (int i=0; i<linhas.length; i++) {
            String palavras[] = linhas[i].split(" ");
            for (int j=0; j<palavras.length; j++) {
                if (isChord(palavras[j], indiceTranspose)) {
                    novoTexto.append(chord);
                }else if (isNote(palavras[j], indiceTranspose)){
                    novoTexto.append(note);
                    novoTexto.append(" ");
                }else{
                    novoTexto.append(palavras[j]);
                    novoTexto.append(" ");
                }
            }
            novoTexto.append(System.getProperty("line.separator"));
        }
        txtLer.setText(novoTexto.toString());
    }

    private boolean isNote(String str, int indiceTranspose){
        boolean retorno = false;
        String novaNota = " ";
        String todasNotas = "do,do#,re,re#,mi,fa,fa#,sol,sol#,la,la#,si,dó,dó#,ré,ré#,mi,fá,fá#,sol,sol#,lá,lá#,si,do,reb,re,mib,mi,fa,solb,sol,lab,la,sib,si,dó,réb,ré,mib,mi,fá,solb,sol,láb,lá,sib,si";
        String arrayNotas[] = todasNotas.split(",");
        int indiceNota = -1;
        for (int i = 0;i<arrayNotas.length;i++){
            if (str.equals(arrayNotas[i])){
                indiceNota = i;
                retorno = true;
                break;
            }
        }
        if (indiceNota + indiceTranspose < 0 && indiceNota >= 0){
            note = arrayNotas[indiceNota + indiceTranspose +12];
        }else if (indiceNota >= 0){
            note = arrayNotas[indiceNota + indiceTranspose];
        }else{
            note = " ";
        }
        return retorno;
    }

    private boolean isChord(String str, int indiceTranspose){
        boolean retorno = false;
        boolean raiz = true;
        StringBuilder raizAcorde = new StringBuilder();
        StringBuilder baixoAcorde = new StringBuilder();
        StringBuilder complAcorde = new StringBuilder();
        StringBuilder complBaixo = new StringBuilder();
        StringBuilder prefixo = new StringBuilder();
        StringBuilder prefixoBaixo = new StringBuilder();
        char charAnterior = ' ';
        char charPosterior = ' ';
        String naoPertenceAcorde = "HIJKLNOPQRSTUVWXYZcefhklnopqrtvwxyz";

        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (i+1<str.length()){
                charPosterior = str.charAt(i+1);
            }else{
                charPosterior = ' ';
            }
            if (i==0){
                if (c == 'A' || c == 'B' ||c == 'C' ||c == 'D' ||c == 'E' ||c == 'F' ||c == 'G'){
                    raizAcorde.append(c);
                    retorno = true;
                }else if(c=='(' || c=='[') {
                    prefixo.append(c);
                }else{
                    break;
                }
            }
            if (i>0){
                if (c == 'A' || c == 'B' ||c == 'C' ||c == 'D' ||c == 'E' ||c == 'F' ||c == 'G'){
                    if (raiz){
                        raizAcorde.append(c);
                        retorno = true;
                    }else{
                        baixoAcorde.append(c);
                    }

                }
                if (c == '/'){
                    if (charPosterior == 'A' || charPosterior == 'B' ||charPosterior == 'C' ||charPosterior == 'D' ||charPosterior == 'E' ||charPosterior == 'F' ||charPosterior == 'G'){
                        prefixoBaixo.append(c);
                        raiz = false;
                    }else{
                        complAcorde.append(c);
                    }
                }
                if(c == '#' || c == 'b'){
                    if (raiz){
                        raizAcorde.append(c);
                    }else{
                        baixoAcorde.append(c);
                    }
                    retorno = true;
                }
                if (Character.isDigit(charAnterior) && (c == '#' || c == 'b') ){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                    retorno = true;
                }
                if(c == 'm' ||c == 'M' ||c == 'º' ||c == 'd' || c == '°'){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                }
                if (c == 'a' && charAnterior == 'M'){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                }
                if (c == 'j' && charAnterior == 'a'){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                }
                if (c == 'i' && charAnterior == 'd'){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                }
                if(c==')' || c==']'){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                }
                if(Character.isDigit(c)){
                    if (raiz){
                        complAcorde.append(c);
                    }else{
                        complBaixo.append(c);
                    }
                }
                if (naoPertenceAcorde.contains(Character.toString(c))){
                    retorno = false;
                    break;
                }
            }
            charAnterior = c;
        }
        if (!baixoAcorde.toString().equals("")){
            complBaixo.append(' ');
        }
        if (retorno){
            StringBuilder tempChord = new StringBuilder();
            tempChord.append(prefixo);
            tempChord.append(transpose(indiceTranspose,raizAcorde.toString()));
            tempChord.append(complAcorde);
            tempChord.append(prefixoBaixo);
            tempChord.append(transpose(indiceTranspose,baixoAcorde.toString()));
            tempChord.append(complBaixo);
            chord = tempChord.toString();
        }else{
            chord = " ";
        }
        return retorno;
    }

    private String transpose(int indiceTranspose, String acorde){
        String novoAcorde = " ";
        String todosAcordes = "C,C#,D,D#,E,F,F#,G,G#,A,A#,B,C,C#,D,D#,E,F,F#,G,G#,A,A#,B,C,Db,D,Eb,E,F,Gb,G,Ab,A,Bb,B,C,Db,D,Eb,E,F,Gb,G,Ab,A,Bb,B";
        String arrayAcordes[] = todosAcordes.split(",");
        int indiceAcorde = 0;
        for (int i = 0;i<arrayAcordes.length;i++){
            if (acorde.equals(arrayAcordes[i])){
                indiceAcorde = i;
                break;
            }
        }
        if (indiceAcorde + indiceTranspose < 0 && !acorde.equals("")){
            novoAcorde = arrayAcordes[indiceAcorde + indiceTranspose +12];
        }else if (!acorde.equals("")){
            novoAcorde = arrayAcordes[indiceAcorde + indiceTranspose];
        }
        return novoAcorde;
    }

}
