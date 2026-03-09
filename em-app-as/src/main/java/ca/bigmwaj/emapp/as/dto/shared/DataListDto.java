package ca.bigmwaj.emapp.as.dto.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataListDto<T extends AbstractBaseDto> {

    private SearchInfos searchInfos = new SearchInfos();

    private List<T> data;

    public DataListDto(List<T> data) {
        this.data = data;
    }
}
