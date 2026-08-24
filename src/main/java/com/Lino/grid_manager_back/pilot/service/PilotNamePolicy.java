package com.Lino.grid_manager_back.pilot.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import com.Lino.grid_manager_back.pilot.entity.BlockedPilotNameTerm;
import com.Lino.grid_manager_back.pilot.exception.InvalidPilotNameException;
import com.Lino.grid_manager_back.pilot.repository.BlockedPilotNameTermRepository;

@Service
public class PilotNamePolicy {
    private static final Pattern VALID_NAME = Pattern.compile("[\\p{L}\\p{M}]+(?:[ '\\-][\\p{L}\\p{M}]+)*");
    private final BlockedPilotNameTermRepository blockedTermRepository;
    private final ObjectProvider<PilotNameModerationClient> moderationClientProvider;

    public PilotNamePolicy(BlockedPilotNameTermRepository blockedTermRepository,
            ObjectProvider<PilotNameModerationClient> moderationClientProvider) {
        this.blockedTermRepository = blockedTermRepository;
        this.moderationClientProvider = moderationClientProvider;
    }

    public String normalizeAndValidate(String rawName) {
        String normalized = normalize(rawName);
        if (!VALID_NAME.matcher(normalized).matches()) {
            throw new InvalidPilotNameException("Nome de piloto possui caracteres ou formato inválido.");
        }
        if (blockedTermRepository.findAllByOrderByNormalizedTermAsc().stream()
                .map(BlockedPilotNameTerm::getNormalizedTerm).anyMatch(normalized.toLowerCase(Locale.ROOT)::contains)) {
            throw new InvalidPilotNameException("Nome de piloto contém termo não permitido.");
        }
        PilotNameModerationClient moderationClient = moderationClientProvider.getIfAvailable();
        if (moderationClient != null && moderationClient.isFlagged(normalized)) {
            throw new InvalidPilotNameException("Nome de piloto reprovado pela moderação de conteúdo.");
        }
        return normalized;
    }

    private String normalize(String rawName) {
        if (rawName == null) {
            throw new InvalidPilotNameException("Nome de piloto é obrigatório.");
        }
        return Normalizer.normalize(rawName, Normalizer.Form.NFKC).trim().replaceAll("\\s+", " ");
    }
}
