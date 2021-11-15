package ru.bcs.creditmarkt.strapi.dto;

import com.poiji.annotation.ExcelCellName;
import io.github.millij.poi.ss.model.annotations.SheetColumn;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankDictionary {
    Long id;
    @ExcelCellName("Название")
    String  name;
    @ExcelCellName("Регион")
    String region;
    @ExcelCellName("Город")
    String city;
    @ExcelCellName("Адрес")
    String address;
    @ExcelCellName("Координаты")
    String latitude;
    @ExcelCellName("Координаты")
    String longitude;
    @ExcelCellName("Подрубрика")
    String subheading;
    @ExcelCellName("Время работы")
    String workingHours;
    @ExcelCellName("Телефон")
    String telephones;
}
