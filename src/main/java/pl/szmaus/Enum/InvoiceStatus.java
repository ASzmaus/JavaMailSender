package pl.szmaus.Enum;


public enum InvoiceStatus {

     SENDING_INVOICE("WYSŁANO FAKTURĘ"),
     START_SENDING_INV("ROZPOCZĘTO WYSYŁANIE FV"),
     PAID("OPŁACONA"),
     REMAINDER1("PRZYPOMNIENIE1"),
     START_SENDING_REMAINDER1("ROZPOCZĘTO WYSYŁANIE PRZYPOMNIENIA1"),
     REMAINDER2("PRZYPOMNIENIE2"),
     START_SENDING_REMAINDER2("ROZPOCZĘTO WYSYŁANIE PRZYPOMNIENIA2"),
     PAID_TO_SEND("OPŁACONA I DO WYSŁANIA");

     public final String label;

     private InvoiceStatus(String label) {
          this.label = label;
     }
}
