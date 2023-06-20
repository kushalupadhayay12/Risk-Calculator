package com.example.dynamic.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "risk_calc_logic")
public class RiskCalculationLogic {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
    private String elementName;
	@Column
    private String formula;
	
	public RiskCalculationLogic() {}

    public RiskCalculationLogic(String elementName, String formula) {
        this.elementName = elementName;
        this.formula = formula;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
