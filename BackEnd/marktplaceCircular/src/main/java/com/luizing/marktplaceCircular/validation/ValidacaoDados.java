package com.luizing.marktplaceCircular.validation;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.UserDto;
import com.luizing.marktplaceCircular.dtos.UserLoginDto;
import java.net.URI;
import java.net.URISyntaxException;

public final class ValidacaoDados {

    private static final int TITULO_MAXIMO = 100;
    private static final int DESCRICAO_MAXIMA = 1000;
    private static final int URL_IMAGEM_MAXIMA = 2048;
    private static final int LOGIN_MAXIMO = 50;
    private static final int CONTATO_MAXIMO = 254;

    private ValidacaoDados() {
    }

    public static void validarAnuncio(AnuncioDto dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Dados do anuncio sao obrigatorios.");
        }

        validarTextoObrigatorio(dto.titulo(), "Titulo", TITULO_MAXIMO);
        validarTextoObrigatorio(dto.descricao(), "Descricao", DESCRICAO_MAXIMA);

        if (dto.categoria() == null || dto.tipo() == null) {
            throw new DadosInvalidosException("Categoria e tipo sao obrigatorios.");
        }
        if (!Double.isFinite(dto.preco()) || dto.preco() < 0) {
            throw new DadosInvalidosException("Preco deve ser um valor valido maior ou igual a zero.");
        }
        if (dto.usuarioId() == null || dto.usuarioId() <= 0) {
            throw new DadosInvalidosException("Usuario do anuncio e obrigatorio.");
        }

        validarUrlImagem(dto.imagem());
    }

    public static void validarNovoUsuario(UserDto dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Dados do usuario sao obrigatorios.");
        }

        validarCredenciais(dto.login(), dto.senha());
        validarContato(dto.contato());
    }

    public static void validarLogin(UserLoginDto dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Credenciais sao obrigatorias.");
        }

        validarCredenciais(dto.login(), dto.senha());
    }

    private static void validarCredenciais(String login, String senha) {
        validarTextoObrigatorio(login, "Login", LOGIN_MAXIMO);

        if (senha == null || senha.length() < 8 || senha.length() > 72) {
            throw new DadosInvalidosException("Senha deve conter entre 8 e 72 caracteres.");
        }
    }

    private static void validarContato(String contato) {
        validarTextoObrigatorio(contato, "Email para contato", CONTATO_MAXIMO);

        if (!contato.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new DadosInvalidosException("Email para contato deve ser valido.");
        }
    }

    private static void validarTextoObrigatorio(String texto, String campo, int tamanhoMaximo) {
        if (texto == null || texto.isBlank()) {
            throw new DadosInvalidosException(campo + " e obrigatorio.");
        }
        if (texto.length() > tamanhoMaximo) {
            throw new DadosInvalidosException(campo + " deve ter no maximo " + tamanhoMaximo + " caracteres.");
        }
    }

    private static void validarUrlImagem(String imagem) {
        validarTextoObrigatorio(imagem, "URL da imagem", URL_IMAGEM_MAXIMA);

        try {
            URI uri = new URI(imagem);
            String protocolo = uri.getScheme();

            if ((!"http".equalsIgnoreCase(protocolo) && !"https".equalsIgnoreCase(protocolo))
                    || uri.getHost() == null) {
                throw new DadosInvalidosException("URL da imagem deve usar http ou https.");
            }
        } catch (URISyntaxException exception) {
            throw new DadosInvalidosException("URL da imagem deve ser valida.");
        }
    }
}
