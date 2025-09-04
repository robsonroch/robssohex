package br.com.robson.robssohex.interactors.model;

import java.time.LocalDate;
public record CreateUserCmd(String username, String email, String rawPassword, LocalDate dateOfBirth) {}