package com.example.pcparts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pcparts.common.BusinessException;
import com.example.pcparts.common.PageResponse;
import com.example.pcparts.dto.customer.CustomerCreateRequest;
import com.example.pcparts.dto.customer.CustomerUpdateRequest;
import com.example.pcparts.entity.Customer;
import com.example.pcparts.mapper.CustomerMapper;
import com.example.pcparts.vo.customer.CustomerVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;

    public PageResponse<CustomerVO> page(long page, long size, String keyword) {
        Page<Customer> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Customer> query = new LambdaQueryWrapper<Customer>();
        if (StringUtils.hasText(keyword)) {
            query.and(w -> w.like(Customer::getName, keyword)
                    .or()
                    .like(Customer::getPhone, keyword));
        }
        query.orderByDesc(Customer::getId);
        Page<Customer> result = customerMapper.selectPage(mpPage, query);
        List<CustomerVO> items = result.getRecords().stream().map(this::toVo).toList();
        return new PageResponse<>(result.getTotal(), items);
    }

    public CustomerVO getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "not_found");
        }
        return toVo(customer);
    }

    public CustomerVO create(CustomerCreateRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setStatus(request.getStatus());
        customerMapper.insert(customer);
        return getById(customer.getId());
    }

    public CustomerVO update(Long id, CustomerUpdateRequest request) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "not_found");
        }
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setStatus(request.getStatus());
        customerMapper.updateById(customer);
        return getById(id);
    }

    public void delete(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "not_found");
        }
        customerMapper.deleteById(id);
    }

    private CustomerVO toVo(Customer c) {
        return new CustomerVO(c.getId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress(), c.getStatus());
    }
}
