package ru.bcs.creditmarkt.strapi.dto.strapi;

import com.poiji.annotation.ExcelCellName;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankDictionary {
    @ExcelCellName("ID")
    private String id;

    @ExcelCellName("Название")
    private String  name;

    @ExcelCellName("Регион")
    private String region;

    @ExcelCellName("Город")
    private String city;

    @ExcelCellName("Адрес")
    private String address;

    @ExcelCellName("Координаты")
    private String latitude;

    @ExcelCellName("Координаты")
    private String longitude;

    @ExcelCellName("Подрубрика")
    private String subheading;

    @ExcelCellName("Время работы")
    private String workingHours;

    @ExcelCellName("Телефон")
    private String telephones;

    @ExcelCellName("Refill")
    private boolean refill;

    @ExcelCellName("CashReceipt")
    private boolean cashReceipt;

    @ExcelCellName("Slug")
    @NotNull(message = "{slug.notNull}")
    private String slug;

    @ExcelCellName("h1")
    private String h1;

    @ExcelCellName("Бик")
    private String bic;
}
