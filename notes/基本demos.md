## demos

### Base api

API = `http://localhost:7999/api`, 下文用`{{API}}`代指该api

### Mike

> 直接使用jhipster命令生成该实体

#### 对filter信息和pagination信息的尝试

`{{API}}/mikes?name.equals=mike&age.greaterThan=12&page=1&size=1`

### Business -> service(serviceClass), dto(mapstruct)

> 使用jdl定义实体, 并加入serviceClass和dto的尝试

原始的jdl:

```
@filter
@paginate(pagination)
@service(serviceClass)
@dto(mapstruct)
entity Business {
    name String maxlength(255)
    time LocalDate
    uuid String
    rawJson TextBlob
    }
```

#### 对dto的尝试和理解

如果需要dto, 则会在

- `./domain/`下生成`entity`
- 然后在`./service/dto/`下生成`enitiyDTO`
- 并在`./service/mapper/`下生成`enitityMapper`

dto的使用方式:

如为了实现变量名替换, 则首先修改`BusinessDTO`中的字段, 并实现`EntityMapper`中的两个方法:

```java
E toEntity(D dto);

    D toDto(E entity);
```

从而完成对`BusinessDTO <-> Business`的双向转换.

从`BusinessService`中可以注意到, 在存储数据库时, 会调用`toEntity`方法, 在从数据库读出返回时, 会调用`toDto`方法:

而`mapper`类的实现来源于`mapstruct`

关于mapstruct的具体使用方式可以参考官方文档: https://mapstruct.org/documentation/stable/reference/html/#introduction

TODO: 以后有时间慢慢看

```java
// 保存
public BusinessDTO save(BusinessDTO businessDTO){
    log.debug("Request to save Business : {}",businessDTO);
    Business business=businessMapper.toEntity(businessDTO);
    business=businessRepository.save(business);
    return businessMapper.toDto(business);
}

// 查询
public Optional<BusinessDTO> findOne(Long id){
    log.debug("Request to get Business : {}",id);
    return businessRepository.findById(id)
    .map(businessMapper::toDto);
}
```

