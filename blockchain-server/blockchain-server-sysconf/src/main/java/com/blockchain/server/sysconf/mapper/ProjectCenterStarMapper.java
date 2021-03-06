package com.blockchain.server.sysconf.mapper;

import com.blockchain.server.sysconf.entity.ProjectCenterReport;
import com.blockchain.server.sysconf.entity.ProjectCenterStar;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ProjectCenterStarMapper extends Mapper<ProjectCenterStar> {
    int countByProjectId(@Param("projectId") String projectId);
}
