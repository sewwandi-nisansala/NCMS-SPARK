package lk.sparkx.ncms.service;

import lk.sparkx.ncms.dao.Hospital;
import lk.sparkx.ncms.dao.Patient;
import lk.sparkx.ncms.dao.Result;
import lk.sparkx.ncms.repository.HospitalRepository;
import lk.sparkx.ncms.repository.QueueRepo;

import java.util.List;

/**
 * Created by: thisum
 * Date      : 2020-09-02
 * Time      : 20:47
 **/

public class HospitalServiceImpl implements HotelService
{


    @Override
    public Result getBedInformationForThePaient(Patient patient)
    {
        HospitalRepository hospitalRepository = new HospitalRepository();
        List<Hospital> list = hospitalRepository.selectHotelsWithBeds();

        if(list.isEmpty())
        {
            QueueRepo queueRepo = new QueueRepo();
            queueRepo.insertIntoQueue(patient);
        }
        else
            {
                for(Hospital hospital : list)
                {

                }
        }

        Result result = new Result();


        return result;
    }
}
