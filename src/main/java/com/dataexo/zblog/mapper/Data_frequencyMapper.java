package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_frequency;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Data_frequencyMapper {


    List<Data_frequency> findAll();

    void saveData_frequency(Data_frequency data_frequency);

    List<Data_frequency> loadData_frequency(@Param("pager") Pager pager, @Param("param") String param);

    Data_frequency getData_frequencyById(Integer id);

    void deleteData_frequency(Integer id);

    void updateData_frequency(Data_frequency data_frequency);

    int initPage(Pager pager);

    Data_frequency checkExist(String name);
}
