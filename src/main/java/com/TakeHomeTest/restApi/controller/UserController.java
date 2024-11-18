package com.TakeHomeTest.restApi.controller;

import com.TakeHomeTest.restApi.dto.*;
import com.TakeHomeTest.restApi.entity.ServiceLayanan;
import com.TakeHomeTest.restApi.service.AuthService;
import com.TakeHomeTest.restApi.service.LayananService;
import com.TakeHomeTest.restApi.service.UserService;
import com.TakeHomeTest.restApi.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private LayananService layananServices;

    @Autowired
    private AuthService authService;

    private static final Logger logger =LoggerFactory.getLogger(UserController.class);


    @Operation(
            summary = "",
            description = "**API Registration Public (Tidak perlu Token untuk mengaksesnya)** \n\n" +
                    "Digunakan untuk melakukan registrasi User agar bisa Login ke dalam aplikasi. \n\n" +
                    "_Ketentuan_:\n" +
                    "1. Parameter request email harus terdapat validasi format email.\n" +
                    "2. Parameter request password harus memiliki panjang minimal 8 karakter.\n" +
                    "3. Handling Response sesuai dokumentasi Response di bawah."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request Successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 0" +
                                    ", \"message\": \"Registrasi berhasil silahkan login\"" +
                                    ", \"data\": null }")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 102" +
                                    ", \"message\": \"Parameter email tidak sesuai format\"" +
                                    ", \"data\": null }")
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<Void>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        userService.checkDuplicateEmail(request.getEmail());

        userService.register(request);

        return ResponseEntity.ok(
                new GeneralResponse<>(0, "Registrasi berhasil silahkan login", null)
        );
    }


    @Operation(
            summary = "",
            description = "**API Login Public (Tidak perlu Token untuk mengaksesnya)** \n\n" +
                    "Digunakan untuk melakukan login dan mendapatkan authentication berupa JWT (Json Web Token). \n\n" +
                    "_Ketentuan_:\n" +
                    "1. Parameter request email harus terdapat validasi format email.\n" +
                    "2. Parameter request password harus memiliki panjang minimal 8 karakter.\n" +
                    "3. JWT yang digenerate harus memuat payload email dan di set expiration selama 12 jam dari waktu di generate.\n" +
                    "4. Handling Response sesuai dokumentasi Response di bawah."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil Login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 0, \"message\": \"Login Sukses\", \"data\":  {\n" +
                                    "    \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiNTRVdXRjYTdCS0Z" +
                                    "PX0ZUZGZ1bXlJem9zSTRKa1VxUGZVZ0ROSTUwelRTQlo2aHoyY0hKZ1VMb1loM09HUUd0ekQxV3dTX194" +
                                    "aHBNZTE2SGFscVRzcEhjS21UclJ3S2FYYmZob3AzdzFFUHJ2NFdBQmk1c0RpdV9DSnZTSWt2MDFTbEU0Q" +
                                    "U5pbVB0bUx5azZoUzlOalVQNEZaVVpfRVBtcEk4Y3pNc3ZWa2JFPSIsImlhdCI6MTYyNjkyODk3MSwiZX" +
                                    "hwIjoyNTU2MTE4Nzk4fQ.9C9NvhZYKivhGWnrjo4Wr1Rv-wur1wCm0jqfK9XDD8U\"\n" +
                                    "  } }")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 102" +
                                    ", \"message\": \"Parameter email tidak sesuai format\"" +
                                    ", \"data\": null }")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 103" +
                                    ", \"message\": \"Username atau password salah\"" +
                                    ", \"data\": null }")
                    )
            )
    })
    @PostMapping("/login")
    public GeneralResponse<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    }


    @Operation(
            summary = "",
            description = "**API Balance Private (memerlukan Token untuk mengaksesnya)** \n\n" +
                    "Digunakan untuk mendapatkan informasi balance / saldo terakhir dari User. \n\n" +
                    "_Ketentuan_:\n" +
                    "1. Service ini harus menggunakan **Bearer Token JWT** untuk mengaksesnya.\n" +
                    "2. Tidak ada parameter email di query param url ataupun request body, parameter email" +
                    " diambil dari payload JWT yang didapatkan dari hasil login.\n" +
                    "3. Handling Response sesuai dokumentasi Response dibawah."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get Balance / Saldo Berhasil",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 0" +
                                    ", \"message\": \"Get Balance Berhasil\"" +
                                    ", \"data\": { \"balance\": 1000000 } }")
                    )
            )
            ,
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 108" +
                                    ", \"message\": \"Token tidak valid atau kadaluarsa\"" +
                                    ", \"data\": null }")
                    )
            )
    })
    @GetMapping("/balance")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> getBalance(@Parameter(hidden = true)
                                                                           @RequestHeader("Authorization") String authorizationHeader) {
        try {
            jwtUtil.validateToken(authorizationHeader);

            String email = jwtUtil.extractEmail(authorizationHeader);

            BigDecimal balance = userService.getBalance(email);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("balance", balance);

            return ResponseEntity.ok(
                    new GeneralResponse<>(0, "Balance retrieved successfully", responseData)
            );
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            return ResponseEntity.badRequest().body(new GeneralResponse<>(102, e.getMessage(), null));
        }
    }


    @Operation(
            summary = "",
            description = "**API Topup Private (memerlukan Token untuk mengaksesnya)** \n\n" +
                    "Digunakan untuk melakukan top up balance / saldo dari User. \n\n" +
                    "_Ketentuan_:\n" +
                    "1. Service ini harus menggunakan **Bearer Token JWT** untuk mengaksesnya.\n" +
                    "2. Tidak ada parameter email di query param url ataupun request body, parameter email diambil dari " +
                    "payload JWT yang didapatkan dari hasil login.\n" +
                    "3. Setiap kali melakukan Top Up maka balance / saldo dari User otomatis bertambah.\n" +
                    "4. Parameter **amount** hanya boleh angka saja dan tidak boleh lebih kecil dari 0.\n" +
                    "5. Pada saat Top Up set transaction_type di database menjadi **TOPUP**.\n" +
                    "6. Handling Response sesuai dokumentasi Response dibawah."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request Successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 0" +
                                    ", \"message\": \"Top Up Balance berhasil\"" +
                                    ", \"data\": { \"balance\": 2000000 } }")
                    )
            )
            ,
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 102" +
                                    ", \"message\": \"Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0\"" +
                                    ", \"data\": null }")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 108" +
                                    ", \"message\": \"Token tidak tidak valid atau kadaluwarsa\"" +
                                    ", \"data\": null }")
                    )
            )
    })
    @PostMapping("/top-up")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> topUpBalance(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserTopupRequest topUpRequest) {

        try {

            jwtUtil.validateToken(authorizationHeader);

            String email = jwtUtil.extractEmail(authorizationHeader);

            BigDecimal amount = topUpRequest.getTopUpAmount();
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Parameter top_up_amount harus lebih besar dari 0");
            }


            BigDecimal userBalance = userService.topUpBalance(email, amount);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("balance", userBalance);

            return ResponseEntity.ok(new GeneralResponse<>(0, "Top up berhasil", responseData));

        } catch (IllegalArgumentException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("balance", null);
            return ResponseEntity.badRequest().body(new GeneralResponse<>(102, e.getMessage(), errorResponse));
        }
    }


    @Operation(
            summary = "",
            description = "**API Transaction Private (memerlukan Token untuk mengaksesnya)** \n\n" +
                    "Digunakan untuk melakukan transaksi dari services / layanan yang tersedia. \n\n" +
                    "_Ketentuan_:\n" +
                    "1. Service ini harus menggunakan **Bearer Token JWT** untuk mengaksesnya.\n" +
                    "2. Tidak ada parameter email di query param url ataupun request body, parameter email diambil dari " +
                    "payload JWT yang didapatkan dari hasil login.\n" +
                    "3. Setiap kali melakukan Transaksi harus dipastikan balance / saldo mencukupi\n.\n" +
                    "4. Pada saat Transaction set transaction_type di database menjadi **PAYMENT**\n.\n" +
                    "5. Handling Response sesuai dokumentasi Response dibawah.\n" +
                    "6. Response invoice_number untuk formatnya generate bebas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaksi berhasil",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "  \"status\": 0,\n" +
                                    "  \"message\": \"Transaksi berhasil\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"invoice_number\": \"INV17082023-001\",\n" +
                                    "    \"service_code\": \"PLN_PRABAYAR\",\n" +
                                    "    \"service_name\": \"PLN Prabayar\",\n" +
                                    "    \"transaction_type\": \"PAYMENT\",\n" +
                                    "    \"total_amount\": 10000,\n" +
                                    "    \"created_on\": \"2023-08-17T10:10:10.000Z\"\n" +
                                    "  }\n" +
                                    "}")
                    )
            )
            ,
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 102" +
                                    ", \"message\": \"Service atau layanan tidak ditemukan.\"" +
                                    ", \"data\": null }")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"status\": 108" +
                                    ", \"message\": \"Token tidak tidak valid atau kadaluwarsa\"" +
                                    ", \"data\": null }")
                    )
            )
    })
    @PostMapping("/transaction")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> transaction(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserTransactionRequest transactionRequest) {
        try {
            jwtUtil.validateToken(authorizationHeader);
            String email = jwtUtil.extractEmail(authorizationHeader);

            layananServices.checkLayananExist(transactionRequest);

            return userService.createTransaction(email,  transactionRequest.getServiceCode());

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            return ResponseEntity.badRequest().body(new GeneralResponse<>(102, e.getMessage(), null));
        }
    }



    @GetMapping("/services")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> services(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {

            jwtUtil.validateToken(authorizationHeader);

            List<ServiceLayanan> layananList = layananServices.getAllServices();

            List<Map<String, Object>> serviceList = layananList.stream().map(layanan -> {
                Map<String, Object> service = new HashMap<>();
                service.put("service_code", layanan.getServiceCode());
                service.put("service_name", layanan.getServiceName());
                service.put("service_icon", layanan.getServiceIcon());
                service.put("service_tariff", layanan.getServiceTariff());
                return service;
            }).collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", serviceList);

            return ResponseEntity.ok(new GeneralResponse<>(0, "Sukses", responseData));
        } catch (IllegalArgumentException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            return ResponseEntity.badRequest().body(new GeneralResponse<>(102, e.getMessage(), null));
        }
    }


}
