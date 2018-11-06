package tw.org.iii.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

public class Calculator extends Activity {


    String strInput = "0";  //輸入字串暫存
    String strOutput = "";  //輸出字串暫存

    //判斷輸入字串
    private int Check(){
        try {
            strInput = displayInput.getText().toString();

            if( "0".equals(strInput)){
                return 0 ;
            }else if(strInput.endsWith("+")){
                return 1 ;
            }
            else if(strInput.endsWith("-")){
                return 2 ;
            }
            else if(strInput.endsWith("*")){
                return 3 ;
            }
            else if(strInput.endsWith("/")){
                return 4 ;
            }else if(strInput.contains(".")){
                return 7 ;
            }
        }catch(Exception e){

            strInput = "0";
            strOutput = "";
            displayInput.setText(strInput);
            displayOutput.setText(strOutput);
        }
        return -1 ;

    }

    //將輸入的運算字串轉成後序式
    private void ToPostFix(String strOutput) {

        ArrayList<String> alPostFix = new ArrayList<>(); //後序式字串陣列
        String opAS = "";    //+-號暫存器
        String opMD = "";    //*/號暫存器
        String strTemp = "";

        for(int i = 0 ; i <strOutput.length() ; i++ ){
           if("*".equals(String.valueOf(strOutput.charAt(i))) || "/".equals(String.valueOf(strOutput.charAt(i)))){
                if("" != opMD){
                    alPostFix.add(strTemp);
                    strTemp = "";
                    alPostFix.add(opMD);
                    opMD = String.valueOf(strOutput.charAt(i));
                    Log.d("LogCa","MD1");
                }else{
                    alPostFix.add(strTemp);
                    strTemp = "";
                    opMD = String.valueOf(strOutput.charAt(i));
                    Log.d("LogCa","MD2");
                }
            }else if("+".equals(String.valueOf(strOutput.charAt(i))) || "-".equals(String.valueOf(strOutput.charAt(i)))){
                if("" != opAS && "" !=  opMD){
                    alPostFix.add(strTemp);
                    strTemp = "";
                    alPostFix.add(opMD);
                    alPostFix.add(opAS);
                    opMD = "";
                    opAS = String.valueOf(strOutput.charAt(i));
                    Log.d("LogCa","AS1");
                }else if("" != opAS && "" ==  opMD){
                    alPostFix.add(strTemp);
                    strTemp = "";
                    alPostFix.add(opAS);
                    opAS = String.valueOf(strOutput.charAt(i));
                    Log.d("LogCa","AS2");
                }else if("" == opAS && "" !=  opMD){
                    alPostFix.add(strTemp);
                    strTemp = "";
                    alPostFix.add(opMD);
                    opMD = "";
                    opAS = String.valueOf(strOutput.charAt(i));
                    Log.d("LogCa","AS3");
                }else{
                    alPostFix.add(strTemp);
                    strTemp = "";
                    opAS = String.valueOf(strOutput.charAt(i));
                    Log.d("LogCa","AS4");
                }
            }else{
                strTemp += String.valueOf(strOutput.charAt(i));
                Log.d("LogCa","strTemp");
            }
        }
        //將for迴圈裡剩下的暫存器add進alPostFix
        if("" != strTemp){
            alPostFix.add(strTemp);
            Log.d("LogCa","last strTemp");
        }
        if("" != opMD){
            alPostFix.add(opMD);
            Log.d("LogCa","last opMD");
        }
        if("" != opAS){
            alPostFix.add(opAS);
            Log.d("LogCa","last opAS");
        }

        //debug用
        String s = "";
        for(String a : alPostFix){
            s+=" " + a;
        }
        Log.d("LogCa","output " + strOutput );
        Log.d("LogCa","alPostFixLast " +s );

        Computer(alPostFix);
    }

    //使用堆疊將後序式進行計算
    private void Computer(ArrayList<String> alPostFix) {

        Stack<String> staNum = new Stack<>();   //建立數字用堆疊
        double num1 = 0;
        double num2 = 0;

        try{
            //逐一取出，若數字就壓入堆疊，若運算子就從堆疊取出2個數字做運算，運算完再壓入堆疊。
            for(String s : alPostFix){

                switch (s){
                    case "+":{
                        if(true != staNum.empty()){
                            num1 = Double.valueOf(staNum.pop());    //pop()從堆疊頂取出元素，並將其從堆疊移除
                        }
                        if(true != staNum.empty()){
                            num2 = Double.valueOf(staNum.pop());    //peek()從堆疊頂取出元素，但並不移除
                        }
                        staNum.push(String.valueOf(num2 + num1));
                        break;
                    }
                    case "-":{
                        if(true != staNum.empty()){
                            num1 = Double.valueOf(staNum.pop());
                        }
                        if(true != staNum.empty()){
                            num2 = Double.valueOf(staNum.pop());
                        }
                        staNum.push(String.valueOf(num2 - num1));
                        break;
                    }
                    case "*":{
                        if(true != staNum.empty()){
                            num1 = Double.valueOf(staNum.pop());
                        }
                        if(true != staNum.empty()){
                            num2 = Double.valueOf(staNum.pop());
                        }
                        staNum.push(String.valueOf(num2 * num1));
                        break;
                    }
                    case "/":{
                        if(true != staNum.empty()){
                            num1 = Double.valueOf(staNum.pop());
                            if(0 == num1){
                                throw new ArithmeticException();
                            }
                        }
                        if(true != staNum.empty()){
                            num2 = Double.valueOf(staNum.pop());
                        }
                        staNum.push(String.valueOf(num2 / num1));
                        break;
                    }
                    default:{
                        staNum.push(s);
                    }
                }
//
            }
            strOutput = "";
            displayOutput.setText(strOutput);
            strInput = staNum.peek();   //堆疊頂的元素即為運算式的解
            displayInput.setText(strInput);
        }catch(Exception e){
            //輸入錯誤的運算式或超出運算範圍
            Log.d("LogCa","Exception e : " + e );
            AlertDialog.Builder builder = new AlertDialog.Builder(Calculator.this);
            builder.setTitle("錯誤");
            builder.setMessage("輸入錯誤的運算式或超出運算範圍");

            DialogInterface.OnClickListener btnOK_click = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };

            builder.setPositiveButton("確定",btnOK_click);
            Dialog errorMessage = builder.create();
            errorMessage.show();
            btnClear.performClick();
        }

    }

    private View.OnClickListener btn1_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "1";
                displayInput.setText(strInput);
            }else{
                strInput += "1";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn2_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "2";
                displayInput.setText(strInput);
            }else{
                strInput += "2";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn3_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "3";
                displayInput.setText(strInput);
            }else{
                strInput += "3";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn4_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "4";
                displayInput.setText(strInput);
            }else{
                strInput += "4";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn5_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "5";
                displayInput.setText(strInput);
            }else{
                strInput += "5";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn6_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "6";
                displayInput.setText(strInput);
            }else{
                strInput += "6";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn7_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "7";
                displayInput.setText(strInput);
            }else{
                strInput += "7";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn8_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "8";
                displayInput.setText(strInput);
            }else{
                strInput += "8";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn9_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "9";
                displayInput.setText(strInput);
            }else{
                strInput += "9";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btn0_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 0 == Check() ){
                strInput = "0";
                displayInput.setText(strInput);
            }else{
                strInput += "0";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btnAdder_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(1 == Check()||2 == Check()||3 == Check()||4 == Check()){
                strInput = strInput.substring(0,strInput.length()-1) + "+";
                displayInput.setText(strInput);
            }else{
                strOutput = displayOutput.getText().toString() + strInput;
                displayOutput.setText(strOutput);
                strInput = "+";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btnSubtraction_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(1 == Check()||2 == Check()||3 == Check()||4 == Check()){
                strInput = strInput.substring(0,strInput.length()-1) + "-";
                displayInput.setText(strInput);
            }else{
                strOutput = displayOutput.getText().toString() + strInput;
                displayOutput.setText(strOutput);
                strInput = "-";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btnMultiplication_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(1 == Check()||2 == Check()||3 == Check()||4 == Check()){
                strInput = strInput.substring(0,strInput.length()-1) + "*";
                displayInput.setText(strInput);
            }else{
                strOutput = displayOutput.getText().toString() + strInput;
                displayOutput.setText(strOutput);
                strInput = "*";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btnDivision_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(1 == Check()||2 == Check()||3 == Check()||4 == Check()){
                strInput = strInput.substring(0,strInput.length()-1) + "/";
                displayInput.setText(strInput);
            }else{
                strOutput = displayOutput.getText().toString() + strInput;
                displayOutput.setText(strOutput);
                strInput = "/";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btnDot_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( 1 == Check()||2 == Check()||3 == Check()||4 == Check()){
                strInput = displayInput.getText().toString() + "0.";
                displayInput.setText(strInput);
            }else if(7 == Check()){
                //甚麼都不做
            }else{
                strInput += ".";
                displayInput.setText(strInput);
            }
        }
    };

    private View.OnClickListener btnClear_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            strOutput = "";
            strInput = "0";
            displayOutput.setText("");
            displayInput.setText(strInput);
        }
    };

    private View.OnClickListener btnBackSpace_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(false == strInput.isEmpty()){
                //若strInput不是空的
                strInput = strInput.substring(0,strInput.length()-1);
                displayInput.setText(strInput);
                Log.d("LogCa","btnBackSpace_click strInput1 : " + strInput );
                Log.d("LogCa","btnBackSpace_click strOutput1 : " + strOutput );
            }else if(false == strOutput.isEmpty()){
                //若strOutput不是空的
                Log.d("LogCa","btnBackSpace_click strOutput2 : " + strOutput );
                strInput = strOutput.substring(0,strOutput.length()-1);
                Log.d("LogCa","btnBackSpace_click strInput2 : " + strInput );
                displayInput.setText(strInput);
                strOutput = "";
                displayOutput.setText(strOutput);
            }
            //若strInput跟strOutput都是空的，就甚麼都不做
        }
    };

    private View.OnClickListener btnEqual_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(1 == Check()||2 == Check()||3 == Check()||4 == Check()){
                //若最後輸入的是+-*/就直接捨去
                strInput = displayInput.getText().toString();
                strInput = strInput.substring(0,strInput.length()-1);
            }else{
                strInput = displayInput.getText().toString();
            }
            strOutput = displayOutput.getText().toString() + strInput;
            ToPostFix(strOutput);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LOG_DEMO","啟動程式");
        super.onCreate(savedInstanceState);
        Log.d("LOG_DEMO","指定 xml");
        setContentView(R.layout.calculator);
        Log.d("LOG_DEMO","初始化畫面");
        InicialComponent();
        Log.d("LOG_DEMO","就緒");
    }

    private void InicialComponent(){
        displayInput = findViewById(R.id.displayInput);
        displayOutput = findViewById(R.id.displayOutput);
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(btn1_click);
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(btn2_click);
        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(btn3_click);
        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(btn4_click);
        btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(btn5_click);
        btn6 = findViewById(R.id.btn6);
        btn6.setOnClickListener(btn6_click);
        btn7 = findViewById(R.id.btn7);
        btn7.setOnClickListener(btn7_click);
        btn8 = findViewById(R.id.btn8);
        btn8.setOnClickListener(btn8_click);
        btn9 = findViewById(R.id.btn9);
        btn9.setOnClickListener(btn9_click);
        btn0 = findViewById(R.id.btn0);
        btn0.setOnClickListener(btn0_click);
        btnAdder = findViewById(R.id.btnAdder);
        btnAdder.setOnClickListener(btnAdder_click);
        btnSubtraction = findViewById(R.id.btnSubtraction);
        btnSubtraction.setOnClickListener(btnSubtraction_click);
        btnMultiplication = findViewById(R.id.btnMultiplication);
        btnMultiplication.setOnClickListener(btnMultiplication_click);
        btnDivision = findViewById(R.id.btnDivision);
        btnDivision.setOnClickListener(btnDivision_click);
        btnEqual = findViewById(R.id.btnEqual);
        btnEqual.setOnClickListener(btnEqual_click);
        btnDot = findViewById(R.id.btnDot);
        btnDot.setOnClickListener(btnDot_click);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(btnClear_click);
        btnBackSpace = findViewById(R.id.btnBackSpace);
        btnBackSpace.setOnClickListener(btnBackSpace_click);
    }

    TextView displayInput;
    TextView displayOutput;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn8;
    Button btn9;
    Button btn0;
    Button btnAdder;
    Button btnSubtraction;
    Button btnMultiplication;
    Button btnDivision;
    Button btnEqual;
    Button btnDot;
    Button btnClear;
    Button btnBackSpace;

}
