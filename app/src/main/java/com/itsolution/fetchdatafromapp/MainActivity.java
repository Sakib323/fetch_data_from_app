package com.itsolution.fetchdatafromapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private EditText mEditText;
    private CardView mButton;
    private String apiUrl = "https://api.openai.com/v1/completions";
    private Boolean txt_bool=true,img_bool=false;

    public String url= null;

    private String imgUrl="https://api.openai.com/v1/images/generations";
    String apiKey = "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";
    String model = "image-alpha-001";
    int numImages = 1;
    String size = "512x512";
    String responseFormat = "url";

    public TextView question;

    private String accessToken = "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";
    private List < Message > mMessages;
    public SharedPreferences sharedPreferences,sharedPreferences2;
    TextToSpeech textToSpeech;
    public SpeechRecognizer recognizer;
    public int pitch_rate;
    public int speech_rate;
    public String query;
    Boolean btn_click=false;
    Boolean voice_activation=false;
    public String wake_up_word;
    String native_unit=null;
    public Dialog add_car1;
    Window window;
    String subscription_id=null;
    public int number_click;
    Boolean buy_voice=false;
    public  RecyclerView recyclerView;
    public ImageView imageView;
    private boolean show_ads,txt2voice,speech_recognizer=false;

    public RewardedAd mrewardedAd;
    public String rewarded_ad_id=null;

    ImageView profile;
    public Boolean show_pop_up=false,sub;

    public LottieAnimationView loading_img;
    public CardView card_img;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String phone;
    String expire_date;
    public SharedPreferences onstart;
    public TemplateView template;
    public DatabaseReference voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();



        DatabaseReference api=FirebaseDatabase.getInstance().getReference().child("api_key").child("key");

        api.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                apiKey=snapshot.getValue(String.class);
                accessToken=snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        recyclerView=findViewById(R.id.recycler_view);
        imageView=findViewById(R.id.img_holder);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String today_date = dateFormat.format(today);
        SharedPreferences user_info=getSharedPreferences("user_info",MODE_PRIVATE);
        phone=user_info.getString("phone","");
        sub=user_info.getBoolean("need_subscription",false);
        voice=FirebaseDatabase.getInstance().getReference().child("User").child(phone);

        voice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                expire_date=snapshot.child("expire_date").getValue(String.class);


                subscription_id=snapshot.child("subscription").getValue(String.class);


                if(expire_date.contains(today_date) || !subscription_id.contains("null")){

                    if(subscription_id.contains("null")){
                        Toast.makeText(MainActivity.this, "You are enjoying free trial", Toast.LENGTH_SHORT).show();
                        show_ads=true;
                    }
                    txt2voice=true;
                    speech_recognizer=true;



                }
                else {
                    show_ads=true;
                    txt2voice=false;
                    speech_recognizer=false;
                    premium();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });





        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                try {
                    ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = ai.metaData;
                    String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.e("app_id_is", "Name Found: " + myApiKey);

                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("TAG", "Failed to load meta-data, NameNotFound: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("TAG", "Failed to load meta-data, NullPointer: " + e.getMessage());
                }

            }
        });




        template = findViewById(R.id.my_template);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("app_ads");
        DatabaseReference native_ads=databaseReference.child("native_ad");
        native_ads.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                native_unit = snapshot.getValue(String.class);

                if(native_unit.contains("empty") || show_ads==false){
                    template.setVisibility(View.GONE);
                }else{
                    MobileAds.initialize(MainActivity.this);
                    AdLoader adLoader = new AdLoader.Builder(MainActivity.this, native_unit)
                            .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                                @Override
                                public void onNativeAdLoaded(NativeAd nativeAd) {
                                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder().build();
                                    template.setStyles(styles);
                                    template.setNativeAd(nativeAd);
                                }
                            }).build();
                    adLoader.loadAd(new AdRequest.Builder().build());

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });




        onstart=getSharedPreferences("registered",MODE_PRIVATE);

        show_pop_up=onstart.getBoolean("onstart",false);

        if(show_pop_up==true){

            add_car1=new Dialog(MainActivity.this);
            add_car1.setContentView(R.layout.onstart);
            add_car1.getWindow().setBackgroundDrawableResource(R.color.transparent);
            add_car1.setCancelable(true);
            add_car1.show();

            CardView close=add_car1.findViewById(R.id.next_btn);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add_car1.dismiss();
                    SharedPreferences.Editor editor=onstart.edit();
                    editor.putBoolean("onstart",false);
                    editor.apply();

                    Intent intent=new Intent(MainActivity.this,register.class);
                    startActivity(intent);
                }
            });





        }



        sharedPreferences=getSharedPreferences("settings",MODE_PRIVATE);
        wake_up_word =sharedPreferences.getString("wake_up_word","listen").toLowerCase();




        ImageView setting=findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(show_ads==true){
                    load_id();
                }


                Dialog add_car=new Dialog(MainActivity.this);
                add_car.setContentView(R.layout.settings);
                add_car.getWindow().setBackgroundDrawableResource(R.color.transparent);
                add_car.setCancelable(true);
                add_car.show();


                AutoCompleteTextView car_company=add_car.findViewById(R.id.car_company);
                String[] car_comapny_name={"Default Device Voice","Jack","Alice","Tom","Zara"};
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MainActivity.this, com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item,car_comapny_name);
                car_company.setAdapter(arrayAdapter);

                car_company.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        car_company.showDropDown();
                        car_company.requestFocus();
                        return false;
                    }
                });

                AutoCompleteTextView Ai_creativity=add_car.findViewById(R.id.car_model);
                String[] Ai_creativity_1={"High (Recommended) ","Medium","Low"};
                ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(MainActivity.this, com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item,Ai_creativity_1);
                Ai_creativity.setAdapter(arrayAdapter1);
                Ai_creativity.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Ai_creativity.showDropDown();
                        Ai_creativity.requestFocus();
                        return false;
                    }
                });


                SharedPreferences sharedPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor settings_data = sharedPref.edit();
                SeekBar seekBar=add_car.findViewById(R.id.pitch);
                SeekBar seekBar2=add_car.findViewById(R.id.speech_rate);
                EditText wake_up_word=add_car.findViewById(R.id.wake_up_word);
                CardView next_btn=add_car.findViewById(R.id.next_btn);
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                settings_data.putInt("pitch",i);
                                settings_data.apply();
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                settings_data.putInt("speech_rate",i);
                                settings_data.apply();
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        String word=wake_up_word.getText().toString();
                        if(!word.isEmpty()){

                            Toast.makeText(MainActivity.this, "wake up word is "+word, Toast.LENGTH_SHORT).show();
                            settings_data.putString("wake_up_word",word);
                            settings_data.apply();

                        }else {
                            Toast.makeText(MainActivity.this, "you can't left wake up word empty"+word, Toast.LENGTH_SHORT).show();
                        }

                        add_car.dismiss();
                    }
                });

                }
        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                try {
                    speech_rate=Settings.Secure.getInt(getContentResolver(), Settings.Secure.TTS_DEFAULT_RATE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    pitch_rate=Settings.Secure.getInt(getContentResolver(), Settings.Secure.TTS_DEFAULT_PITCH);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                int pitch =sharedPreferences.getInt("pitch",pitch_rate);
                int speech =sharedPreferences.getInt("speech_rate",speech_rate);

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                    //textToSpeech.setVoice(Voice.);
                    //textToSpeech.setSpeechRate(speech);
                    //textToSpeech.setPitch(pitch);
                }
            }
        });


        mRecyclerView = findViewById(R.id.recycler_view);
        mEditText = findViewById(R.id.edit_text);
        mButton = findViewById(R.id.button);
        mMessages = new ArrayList < > ();
        mAdapter = new MessageAdapter(mMessages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        CardView txt,img;

        txt=findViewById(R.id.txt);
        img=findViewById(R.id.img);
        loading_img=findViewById(R.id.loading);
        card_img=findViewById(R.id.img_card);
        question=findViewById(R.id.question);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card_img.setVisibility(View.GONE);
                img.setCardBackgroundColor(Color.BLACK);
                txt.setCardBackgroundColor(Color.RED);
                mEditText.setHint("Explain quantum computing");
                txt_bool=true;
                img_bool=false;
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.GONE);
                img.setCardBackgroundColor(Color.RED);
                txt.setCardBackgroundColor(Color.BLACK);
                mEditText.setHint("Image of a cat sitting on a couch");
                txt_bool=false;
                img_bool=true;
            }
        });


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                number_click=number_click+1;
                if(number_click==5 && show_ads==true){
                    load_id();
                    number_click=0;
                }

                if(txt_bool==true){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    callAPI();
                }
                if(img_bool==true){
                    card_img.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    loading_img.setVisibility(View.VISIBLE);
                    img_generator();
                }


            }
        });

        profile=findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile();
                if(show_ads==true){
                    load_id();
                }

            }
        });

    }

    private void show_ads(){
        if(mrewardedAd!=null){
            mrewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(RewardItem rewardItem) {


                }
            });
        }

    }

    public void load_id(){

        FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference2 = firebaseDatabase2.getReference("app_ads");
        DatabaseReference rewarded_video_ad=databaseReference2.child("rewarded_video_ad");
        rewarded_video_ad.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rewarded_ad_id = snapshot.getValue(String.class);

                if(rewarded_ad_id.contains("empty")){

                }else{
                    load_ads();
                }

                //ca-app-pub-3940256099942544/5224354917

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void load_ads() {
        AdRequest adRequest=new AdRequest.Builder().build();
        RewardedAd.load(MainActivity.this, rewarded_ad_id, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                mrewardedAd=null;

                if (number_click==2){
                    Log.e("now showing"," ads");
                }

            }

            @Override
            public void onAdLoaded(RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                mrewardedAd=rewardedAd;
                show_ads();

                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });
            }
        });

    }


    private void callAPI() {


        template.setVisibility(View.GONE);


        String text= mEditText.getText().toString();

        mMessages.add(new Message(text, true));
        mAdapter.notifyItemInserted(mMessages.size() - 1);



        Log.e("text",text);

        mEditText.getText().clear();
        mRecyclerView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", text);
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 1);
            requestBody.put("top_p", 1);
            requestBody.put("frequency_penalty", 0.0);
            requestBody.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody, new Response.Listener < JSONObject > () {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray choicesArray = response.getJSONArray("choices");
                    JSONObject choiceObject = choicesArray.getJSONObject(0);
                    String text = choiceObject.getString("text");
                    Log.e("API Response", response.toString());


                    if(txt2voice==true){
                        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                    }


                    mMessages.add(new Message(text.replaceFirst("\n", "").replaceFirst("\n", ""), false));
                    mAdapter.notifyItemInserted(mMessages.size() - 1);

                    recyclerView.scrollToPosition(mMessages.size() - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API Error", error.toString());
            }
        }) {
            @Override
            public Map < String, String > getHeaders() throws AuthFailureError {
                Map < String, String > headers = new HashMap < > ();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            protected Response < JSONObject > parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        int timeoutMs = 25000; // 25 seconds timeout
        RetryPolicy policy = new DefaultRetryPolicy(timeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        MySingleton.getInstance(this).addToRequestQueue(request);
    }



    private void img_generator(){

        String img_query= mEditText.getText().toString();
        mEditText.setHint("Image of a cat sitting on a couch");

        mEditText.setText("");
        question.setText(img_query);

        mRecyclerView.setVisibility(View.GONE);
        template.setVisibility(View.GONE);



        // Create a JSON object to represent the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", model);
            requestBody.put("prompt", img_query);
            requestBody.put("num_images", numImages);
            requestBody.put("size", size);
            requestBody.put("response_format", responseFormat);
        } catch (JSONException e) {
            Log.e("TAG", "Failed to create request body: " + e.getMessage());
            return;
        }

        // Define the media type for the request body
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Build the request
        String authorizationHeader = "Bearer " + apiKey;
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .header("Authorization", authorizationHeader)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "Failed to execute request: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(responseBody);
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        JSONObject dataObject = dataArray.getJSONObject(0);
                        url = dataObject.getString("url");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(View.VISIBLE);
                                Picasso.get().load(url).into(imageView);
                                loading_img.setVisibility(View.GONE);
                                Log.e("Url ", url);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                } else {
                    Log.e("TAG", "Request failed with error code: " + response.code());
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void profile(){
        Dialog add_car=new Dialog(MainActivity.this);
        add_car.setContentView(R.layout.profile);
        add_car.getWindow().setBackgroundDrawableResource(R.color.transparent);
        add_car.setCancelable(true);
        add_car.show();

        sharedPreferences2=getSharedPreferences("user_info",MODE_PRIVATE);

        String saved_email=sharedPreferences2.getString("email","");
        String saved_pass=sharedPreferences2.getString("pass","");
        String saved_phone=sharedPreferences2.getString("phone","");


        TextView email,pass,phone,btn_text,buy_pack;
        LinearLayout linearLayout_1,linearLayout_2;

        linearLayout_1=add_car.findViewById(R.id.linear_layout_1);
        linearLayout_1.setVisibility(View.GONE);
        linearLayout_2=add_car.findViewById(R.id.linear_layout_2);
        linearLayout_2.setVisibility(View.VISIBLE);

        email=add_car.findViewById(R.id.email_1);
        pass=add_car.findViewById(R.id.password_1);
        phone=add_car.findViewById(R.id.phone_1);
        buy_pack=add_car.findViewById(R.id.buy_pack);
        btn_text=add_car.findViewById(R.id.textview_btn);
        btn_text.setText("Close");

        buy_pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,billing.class);
                startActivity(intent);
            }
        });


        CardView cardView=add_car.findViewById(R.id.next_btn);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_car.dismiss();
            }
        });
        TextView help=add_car.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_car1=new Dialog(MainActivity.this);
                add_car1.setContentView(R.layout.onstart);
                add_car1.getWindow().setBackgroundDrawableResource(R.color.transparent);
                add_car1.setCancelable(true);
                add_car1.show();

                TextView help=add_car1.findViewById(R.id.wake_up_word2);
                help.setText("If you want to use the app in the background even when it's closed, ensure that you have given permission to access the microphone and to appear on top of other apps. Also, ensure that a dynamic interface is appearing on top of the phone's user interface.Now close the app. This will allow you to use a wake-up word, such as listen, to ask a question. For example, you could say 'listen, how are you?'" );

                CardView close=add_car1.findViewById(R.id.next_btn);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add_car1.dismiss();
                        SharedPreferences.Editor editor=onstart.edit();
                        editor.putBoolean("onstart",false);
                        editor.apply();
                    }
                });

            }
        });

        email.setText(saved_email);
        pass.setText(saved_pass);
        phone.setText(saved_phone);



    }

    private void premium(){

        Dialog add_car2=new Dialog(MainActivity.this);
        add_car2.setContentView(R.layout.onstart);
        add_car2.getWindow().setBackgroundDrawableResource(R.color.transparent);
        add_car2.setCancelable(true);
        add_car2.show();

        LinearLayout linearLayout2=add_car2.findViewById(R.id.wake_up_word);
        linearLayout2.setVisibility(View.GONE);
        LinearLayout linearLayout=add_car2.findViewById(R.id.premium);
        linearLayout.setVisibility(View.VISIBLE);


        CardView close1=add_car2.findViewById(R.id.next_btn);
        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_car2.dismiss();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MainActivity.this,register.class);
        startActivity(intent);
        finish();
    }
}