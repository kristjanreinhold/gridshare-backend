package ee.gridshare.domain;

public enum ChargerType {
    TYPE2, // Mennekes, AC — EU standard
    SCHUKO, // household socket, slow AC
    CEE_BLUE, // 1-phase industrial/"caravan", AC
    CEE_RED, // 3-phase industrial, AC
    CCS, // Combo 2, DC fast
    CHADEMO, // DC, legacy
    TYPE1 // J1772, AC — older/imported cars
}
