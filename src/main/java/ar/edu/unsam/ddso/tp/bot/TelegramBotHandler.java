package ar.edu.unsam.ddso.tp.bot;

import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PageResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceSearchRequestDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.service.PieceSearchService;
import ar.edu.unsam.ddso.tp.service.PieceService;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotHandler {

    static final int RESULTS_PER_MESSAGE = 5;
    static final int MAX_MESSAGE_LENGTH = 4000;
    static final String ERROR_MESSAGE = "Ocurrió un error inesperado. Probá de nuevo más tarde.";

    private static final String HELP_MESSAGE =
            """
            Archivo Digital - comandos:

            /buscar <texto> - busca piezas por nombre
            /tag <nombre> - piezas catalogadas con ese tag
            /pieza <id> - detalle de una pieza con los links de sus digitalizaciones

            También podés escribir directamente el texto a buscar.""";

    private final PieceSearchService pieceSearchService;
    private final PieceService pieceService;

    public TelegramBotHandler(PieceSearchService pieceSearchService, PieceService pieceService) {
        this.pieceSearchService = pieceSearchService;
        this.pieceService = pieceService;
    }

    // ---------- Operations ----------

    public String handle(String text) {
        if (text == null || text.isBlank()) {
            return HELP_MESSAGE;
        }
        String trimmed = text.trim();
        if (!trimmed.startsWith("/")) {
            return searchByName(trimmed);
        }

        int space = trimmed.indexOf(' ');
        String command = space < 0 ? trimmed : trimmed.substring(0, space);
        String argument = space < 0 ? "" : trimmed.substring(space + 1).trim();

        // in groups telegram sends /command@BotName
        int at = command.indexOf('@');
        if (at >= 0) {
            command = command.substring(0, at);
        }
        // /pieza_3 == /pieza 3
        int underscore = command.indexOf('_');
        if (underscore >= 0 && argument.isEmpty()) {
            argument = command.substring(underscore + 1);
            command = command.substring(0, underscore);
        }

        return switch (command.toLowerCase(Locale.ROOT)) {
            case "/start", "/help", "/ayuda" -> HELP_MESSAGE;
            case "/buscar" -> argument.isEmpty() ? "Usá: /buscar <texto>" : searchByName(argument);
            case "/tag" -> argument.isEmpty() ? "Usá: /tag <nombre>" : searchByTag(argument);
            case "/pieza" -> pieceDetail(argument);
            default -> "No conozco ese comando.\n\n" + HELP_MESSAGE;
        };
    }

    // ---------- Private ----------

    private String searchByName(String name) {
        PieceSearchRequestDTO request = new PieceSearchRequestDTO(name, null, null, null, null);
        return formatResults("\"" + name + "\"", search(request));
    }

    private String searchByTag(String tag) {
        PieceSearchRequestDTO request =
                new PieceSearchRequestDTO(null, null, null, null, List.of(tag));
        return formatResults("el tag \"" + tag + "\"", search(request));
    }

    private PageResponseDTO<PieceResponseDTO> search(PieceSearchRequestDTO request) {
        return pieceSearchService.search(request, 0, RESULTS_PER_MESSAGE);
    }

    private String pieceDetail(String argument) {
        long id;
        try {
            id = Long.parseLong(argument);
        } catch (NumberFormatException ex) {
            return "Usá: /pieza <id> (por ejemplo /pieza 3)";
        }
        PieceResponseDTO piece;
        try {
            piece = pieceService.getPieceById(id);
        } catch (ResourceNotFoundException ex) {
            return "No existe la pieza " + id + ".";
        }
        return truncate(formatDetail(piece));
    }

    private String formatResults(String searched, PageResponseDTO<PieceResponseDTO> page) {
        if (page.content().isEmpty()) {
            return "No encontré piezas para " + searched + ".";
        }
        StringBuilder text = new StringBuilder();
        text.append("Encontré ").append(page.totalElements()).append(" pieza(s) para ").append(searched);
        if (page.totalElements() > page.content().size()) {
            text.append(". Te muestro las primeras ").append(page.content().size());
        }
        text.append(":\n");
        for (PieceResponseDTO piece : page.content()) {
            text.append("\n• ").append(piece.name());
            if (piece.date() != null) {
                text.append(" (").append(piece.date()).append(')');
            }
            text.append("\n  Ver detalle: /pieza_").append(piece.id()).append('\n');
        }
        return truncate(text.toString());
    }

    private String formatDetail(PieceResponseDTO piece) {
        StringBuilder text = new StringBuilder(piece.name()).append('\n');
        if (piece.date() != null) {
            text.append("Fecha: ").append(piece.date()).append('\n');
        }
        if (piece.description() != null && !piece.description().isBlank()) {
            text.append('\n').append(piece.description()).append('\n');
        }
        List<DigitizationResponseDTO> digitizations = piece.digitizations();
        if (digitizations.isEmpty()) {
            return text.append("\nEsta pieza todavía no tiene digitalizaciones.").toString();
        }
        text.append("\nDigitalizaciones (").append(digitizations.size()).append("):\n");
        int number = 1;
        for (DigitizationResponseDTO digitization : digitizations) {
            text.append(number++).append(". ");
            if (digitization.description() != null && !digitization.description().isBlank()) {
                text.append(digitization.description()).append("\n   ");
            }
            text.append(digitization.url()).append('\n');
        }
        return text.toString();
    }

    private String truncate(String text) {
        if (text.length() <= MAX_MESSAGE_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_MESSAGE_LENGTH - 3) + "...";
    }
}
