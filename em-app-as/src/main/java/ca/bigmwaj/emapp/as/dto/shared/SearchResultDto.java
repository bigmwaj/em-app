package ca.bigmwaj.emapp.as.dto.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.dm.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto<T extends BaseDto> {

    private SearchInfos searchInfos = new SearchInfos();

    private List<T> data;

    public SearchResultDto(List<T> data) {
        this.data = data;
    }
}
