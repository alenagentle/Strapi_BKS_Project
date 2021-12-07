package ru.bcs.creditmarkt.strapi.dto.strapi;

import com.poiji.annotation.ExcelCellName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankDictionary {
    private Long id;
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
}
