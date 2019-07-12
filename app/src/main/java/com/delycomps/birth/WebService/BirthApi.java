package com.delycomps.birth.WebService;

import com.delycomps.birth.Entidades.Contacto;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface BirthApi {

        @FormUrlEncoded
        @POST("/birth/api/send_sms.php")
        Call<RegisterResponse> postRegisterPhoneNumber(@Field("to") String to);

        @FormUrlEncoded
        @POST("/birth/api/birth/consultaNumber")
        Call<RegisterResponse> postConsultaNumber(@Field("phonenumber") String phonenumber);

        @FormUrlEncoded
        @POST("/birth/api/birth/verifyContactos2")
        Call<List<Contacto>> verifyContactos(@Field("arrayContactos") String arrayContactos, @Field("phonenumber") String phonenumber);

        @FormUrlEncoded
        @POST("/birth/api/birth/deleteUsuario")
        Call<RegisterResponse> deleteUsuario(@Field("phonenumber") String phonenumber);

        @Multipart
        @POST("/birth/api/birth/registerInformation")
        Call<RegisterResponse> uploadImage(
                @Part MultipartBody.Part file,
                @Part("name") RequestBody requestBody,
                @Part("names") RequestBody names,
                @Part("surnames") RequestBody surnames,
                @Part("birthday") RequestBody  birthday,
                @Part("hideYear") RequestBody  hideYear,
                @Part("phonenumber") RequestBody  phonenumber,
                @Part("tokenFB") RequestBody  tokenFB
        );
        @Multipart
        @POST("/birth/api/birth/registerContactoFalso")
        Call<RegisterResponse> registerContactoFalso(
                @Part MultipartBody.Part file,
                @Part("name") RequestBody requestBody,
                @Part("names") RequestBody names,
                @Part("birthday") RequestBody  birthday,
                @Part("hideYear") RequestBody  hideYear,
                @Part("phonenumber") RequestBody  phonenumber
        );
}
