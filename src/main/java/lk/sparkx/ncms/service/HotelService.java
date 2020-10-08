package lk.sparkx.ncms.service;

import lk.sparkx.ncms.dao.Hospital;
import lk.sparkx.ncms.dao.Patient;
import lk.sparkx.ncms.dao.Result;

import java.util.List;

public interface HotelService
{
    public Result getBedInformationForThePaient(Patient patient);
}
