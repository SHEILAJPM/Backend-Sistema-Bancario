package com.prestamos.dto.dashboard;

import java.math.BigDecimal;

public record FlujoCajaMesResponse(String mes, BigDecimal total) {}