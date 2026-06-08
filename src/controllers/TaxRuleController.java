package controllers;

import exceptions.EntityNotFoundException;
import models.TaxRule;
import models.enums.TaxType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TaxRuleController {

    private static TaxRuleController instance;
    private HashMap<UUID, TaxRule> taxRules;

    private TaxRuleController() {
        this.taxRules = new HashMap<>();
    }

    public static TaxRuleController getInstance() {
        if (instance == null) instance = new TaxRuleController();
        return instance;
    }

    public TaxRule createTaxRule(TaxType taxType, float defaultPercentage, float minimumTaxableAmount) {
        TaxRule rule = new TaxRule(taxType, defaultPercentage, minimumTaxableAmount);
        taxRules.put(rule.getId(), rule);
        return rule;
    }

    public TaxRule updateTaxRule(UUID id, float defaultPercentage, float minimumTaxableAmount)
            throws EntityNotFoundException {
        TaxRule rule = findById(id);
        rule.setDefaultPercentage(defaultPercentage);
        rule.setMinimumTaxableAmount(minimumTaxableAmount);
        return rule;
    }

    public TaxRule findByTaxType(TaxType taxType) throws EntityNotFoundException {
        for (TaxRule rule : taxRules.values()) {
            if (rule.getTaxType() == taxType) return rule;
        }
        throw new EntityNotFoundException("Regla impositiva", taxType.name());
    }

    public List<TaxRule> getAllTaxRules() {
        return new ArrayList<>(taxRules.values());
    }

    private TaxRule findById(UUID id) throws EntityNotFoundException {
        TaxRule rule = taxRules.get(id);
        if (rule == null) throw new EntityNotFoundException("Regla impositiva", id);
        return rule;
    }
}
