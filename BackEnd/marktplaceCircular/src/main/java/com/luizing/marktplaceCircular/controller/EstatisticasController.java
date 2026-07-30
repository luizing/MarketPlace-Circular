package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.EstatisticasResponseDto;
import com.luizing.marktplaceCircular.service.EstatisticasService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estatisticas")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://market-place-circular.vercel.app"
})
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    public EstatisticasController(EstatisticasService estatisticasService) {
        this.estatisticasService = estatisticasService;
    }

    @GetMapping
    public EstatisticasResponseDto consultar() {
        return estatisticasService.consultar();
    }
}
