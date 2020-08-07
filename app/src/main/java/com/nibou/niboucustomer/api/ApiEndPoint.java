package com.nibou.niboucustomer.api;


import com.nibou.niboucustomer.models.*;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.List;


public interface ApiEndPoint {

    @Headers("Content-Type: application/json")
    @POST("oauth/token?")
    Call<AccessTokenModel> getAccessToken(@Header("X-App-Lang") String language, @Query("client_id") String client_id, @Query("client_secret") String client_secret, @Query("grant_type") String grant_type, @Query("username") String username, @Query("password") String password, @Query("account_type") String account_type);

    @Headers("Content-Type: application/json")
    @POST("oauth/token?")
    Call<AccessTokenModel> getRefreshAccessToken(@Header("X-App-Lang") String language, @Query("client_id") String client_id, @Query("client_secret") String client_secret, @Query("grant_type") String grant_type, @Query("refresh_token") String refresh_token);

    @Headers("Content-Type: application/json")
    @GET("v1/users/me")
    Call<ProfileModel> getMyProfile(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Query("include") String include);


    @Headers("Content-Type: application/json")
    @PUT("v1/users/me/set_status")
    Call<ResponseBody> logoutRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<Object, Object> map);


    @Headers("Content-Type: application/json")
    @GET("v1/users/{id}")
    Call<ProfileModel> getExpertDetails(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("id") String id, @Query("include") String include);

    @Headers("Content-Type: application/json")
    @PUT("v1/users/me")
    Call<ProfileModel> updateMyProfile(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @POST("users")
    Call<ProfileModel> signup(@Header("X-App-Lang") String language, @Body HashMap<String, Object> map);

    @Headers("Content-Type: application/json")
    @PUT("v1/users/me/password")
    Call<GeneralResponseModel> changePassword(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @GET("v1/users/password/change")
    Call<String> forgotPassword(@Header("X-App-Lang") String language, @Query("email") String email);

    @Headers("Content-Type: application/json")
    @GET("v1/expertises")
    Call<SurveyModel> getExpertise(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization);

    @Headers("Content-Type: application/json")
    @POST("v1/expertises")
    Call<ExpertiseModel> addExpertise(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @GET("v1/experts")
    Call<ProfileModel> getExpert(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Nullable @Query("gender") String gender, @Nullable @Query("expert_id") String expert_id, @Query("surveys[]") List<String> surveys);

    @Headers("Content-Type: application/json")
    @GET("v1/experts/latest")
    Call<PreviousExpertModel> getPrevoiusSpokenExpert(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Nullable @Query("gender") String gender, @Nullable @Query("expert_id") String expert_id, @Query("surveys[]") List<String> surveys);

    @Headers("Content-Type: application/json")
    @GET("v1/chat/rooms")
    Call<ActiveChatSessionModel> getActiveChatSession(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization);

    @Headers("Content-Type: application/json")
    @GET("v1/chat/message/{room_id}")
    Call<MessageHistoryModel> getMessages(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("room_id") String room_id);

    @Multipart
    @POST("v1/chat/message/{room_id}")
    Call<MessageModel> sendMessage(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("room_id") String room_id, @Nullable @Part("text") RequestBody text, @Nullable @Part List<MultipartBody.Part> images);

    @Headers("Content-Type: application/json")
    @POST("v1/reviews")
    Call<ResponseBody> sendReview(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, String> map);

    @FormUrlEncoded
    @POST("v1/chat/rooms")
    Call<ResponseBody> createChatSession(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Field("expert_id") String expert_id, @Field("expertise_ids[]") List<String> expertise_ids);

    @FormUrlEncoded
    @POST("v1/chat/rooms")
    Call<ResponseBody> createChatWithHistorySession(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Field("expert_id") String expert_id, @Field("expertise_ids[]") List<String> expertise_ids, @Field("is_private") String is_private);

    @Headers("Content-Type: application/json")
    @PUT("v1/chat/rooms/{id}")
    Call<ChatSessionModel> switchExpert(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("id") String room_id, @Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @DELETE("v1/chat/rooms/{id}")
    Call<ResponseBody> endSession(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("id") String room_id);

    @Headers("Content-Type: application/json")
    @GET("v1/chat/rooms/{id}")
    Call<ChatSessionModel> checkRoomOpenOrClosedRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("id") String room_id);

    @Headers("Content-Type: application/json")
    @POST("v1/feedbacks")
    Call<ResponseBody> sendFeedback(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @GET("v1/reviews/{expert_id}")
    Call<ReviewModel> getReviewRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("expert_id") String expert_id);

    @Headers("Content-Type: application/json")
    @PUT("v1/users/me/devises")
    Call<ResponseBody> saveDevicesRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @PUT("v1/users/me/card")
    Call<ResponseBody> saveCardRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Body HashMap<String, Object> map);

    @Headers("Content-Type: application/json")
    @DELETE("v1/users/me/card/{id}")
    Call<ResponseBody> deleteCreditCard(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("id") String id);

    @Headers("Content-Type: application/json")
    @PUT("v1/users/me/card/{id}/default")
    Call<ResponseBody> markCardDefaultRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Path("id") String id);

    @Headers("Content-Type: application/json")
    @GET("v1/system_texts")
    Call<ProfileModel> getTextRequest(@Header("X-App-Lang") String language, @Query("code") String code);

    @Headers("Content-Type: application/json")
    @GET("v1/payments/month_per_day")
    Call<PaymentListModel> getMonthDaysEarningRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Query("date") String date);

    @Headers("Content-Type: application/json")
    @GET("v1/payments/overall_by_day")
    Call<PaymentListModel> getOverallByDayEarningRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Query("payed") boolean payed, @Query("page[number]") int page_number, @Query("page[size]") int page_size);

    @Headers("Content-Type: application/json")
    @GET("v1/payments/by_day")
    Call<PaymentListModel> getDayEarningDetailRequest(@Header("X-App-Lang") String language, @Header("Authorization") String Authorization, @Query("day") String day, @Query("payed") boolean payed, @Query("page[number]") int page_number, @Query("page[size]") int page_size);
}