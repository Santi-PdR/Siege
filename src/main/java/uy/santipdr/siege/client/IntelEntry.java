package uy.santipdr.siege.client;

public record IntelEntry(String code, String name, String category, int threat, String armament,
                         String variants, String status, String description, String advisory) {}

