

INSERT ignore INTO `stretchy_report` (`id`,`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) VALUES (Null,'Monthly Net Activations','Table',NULL,'Client','select * from v_netact_summary ','Monthly Net Activations',1,1);
INSERT ignore INTO `stretchy_report` (`id`,`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) VALUES (Null,'Monthly Planwise Net Activations','Table',NULL,'Client','select * from v_netactpln_summary ','Monthly Planwise Net Activations',1,1);
INSERT ignore  INTO `stretchy_report` (`id`,`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) VALUES (Null,'Net Activative Details','Table',NULL,'Client','select * from v_netact_dtls \r\nwhere  op_Active =1 or (op_active=0 and Cl_active=1)','Net Activative Details',1,1);

CREATE OR REPLACE VIEW `v_netact_dtls` AS select `dt`.`year4` AS `year4`,`dt`.`year_month_abbreviation` AS `year_mon`,`dt`.`month_number` AS `month_number`,`fdm`.`date_value` AS `fdm`,`ldm`.`date_value` AS `ldm`,`o`.`client_id` AS `client_id`,`o`.`id` AS `order_id`,`o`.`plan_id` AS `plan_id`,`pm`.`plan_description` AS `plan`,`t`.`actual_date` AS `actual_date`,`t`.`transaction_type` AS `transaction_type`,(case when (`op`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN')) then 1 else 0 end) AS `Op_Active`,if((`t`.`transaction_type` = 'ACTIVATION'),1,0) AS `new`,if(((`t`.`transaction_type` = 'RECONNECTION') and (`op`.`transaction_type` = 'DISCONNECTION')),1,0) AS `rec`,if((`t`.`transaction_type` = 'CHANGE_PLAN'),1,0) AS `chg`,if((`t`.`transaction_type` in ('Renewal','RENEWAL','RENEWAL AFTER AUTOEXIPIRY')),1,0) AS `ren`,if(((`dt`.`month_number` = month(`o`.`active_date`)) and (`o`.`order_status` = 4) and (`t`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN'))),1,0) AS `Pending_add`,if(((`dt`.`month_number` >= month(`o`.`active_date`)) and (`o`.`order_status` = 4) and (`op`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN'))),1,0) AS `Op_Pending_add`,if(((`t`.`transaction_type` = 'DISCONNECTION') and (`op`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN'))),1,0) AS `Del`,if(((`dt`.`month_number` = month(`o`.`active_date`)) and (`o`.`order_status` = 4) and (`t`.`transaction_type` = 'DISCONNECTION')),1,0) AS `Pending_del`,if(((`dt`.`month_number` >= month(`o`.`active_date`)) and (`o`.`order_status` = 4)),1,0) AS `Cum_Pending`,(case when ((`cl`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN')) and if((`dt`.`month_number` = month(now())),(`o`.`order_status` = 1),1)) then 1 else 0 end) AS `Cl_Active` from ((((((((`m_client` `c` join `dim_date` `dt` on(((`dt`.`month_number` <= month(now())) and (`dt`.`is_first_day_in_month` = 'Yes')))) join `dim_date` `fdm` on(((`fdm`.`year_month_number` = `dt`.`year_month_number`) and (`fdm`.`is_first_day_in_month` = 'Yes')))) join `dim_date` `ldm` on(((`fdm`.`year_month_number` = `ldm`.`year_month_number`) and (`ldm`.`is_last_day_in_month` = 'Yes')))) join `b_orders` `o` on((`c`.`id` = `o`.`client_id`))) join `b_plan_master` `pm` on((`o`.`plan_id` = `pm`.`id`))) left join `b_orders_history` `op` on(((`o`.`id` = `op`.`order_id`) and (`op`.`id` = (select max(`h3`.`id`) from `b_orders_history` `h3` where ((`h3`.`order_id` = `o`.`id`) and (`h3`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','DISCONNECTION','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN')) and (cast(`h3`.`actual_date` as date) < `fdm`.`date_value`))))))) left join `b_orders_history` `t` on(((`o`.`id` = `t`.`order_id`) and (`t`.`id` = (select max(`h4`.`id`) from `b_orders_history` `h4` where ((`h4`.`order_id` = `o`.`id`) and (`h4`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','DISCONNECTION','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN')) and (cast(`h4`.`actual_date` as date) between `fdm`.`date_value` and `ldm`.`date_value`))))))) left join `b_orders_history` `cl` on(((`o`.`id` = `cl`.`order_id`) and (`cl`.`id` = (select max(`h5`.`id`) from `b_orders_history` `h5` where ((`h5`.`order_id` = `o`.`id`) and (`h5`.`transaction_type` in ('ACTIVATION','RECONNECTION','RENEWAL','DISCONNECTION','Renewal','RENEWAL BEFORE AUTOEXIPIRY','RENEWAL AFTER AUTOEXIPIRY','CHANGE_PLAN')) and (cast(`h5`.`actual_date` as date) <= `ldm`.`date_value`))))))) where (`dt`.`year4` = 2015);
CREATE OR REPLACE VIEW `v_netact_summary` AS select `v_netact_dtls`.`year_mon` AS `Month`,sum(`v_netact_dtls`.`Op_Active`) AS `Op_Bal`,sum(`v_netact_dtls`.`new`) AS `New`,sum(`v_netact_dtls`.`rec`) AS `Reconn`,sum(`v_netact_dtls`.`ren`) AS `Renewal`,(sum(`v_netact_dtls`.`Op_Pending_add`) + sum(`v_netact_dtls`.`Pending_add`)) AS `Pending_add`,sum(((((`v_netact_dtls`.`new` + `v_netact_dtls`.`rec`) + `v_netact_dtls`.`ren`) + `v_netact_dtls`.`Op_Pending_add`) + `v_netact_dtls`.`Pending_add`)) AS `NetAdditions`,sum(((((`v_netact_dtls`.`Op_Active` + `v_netact_dtls`.`new`) + `v_netact_dtls`.`rec`) + `v_netact_dtls`.`Op_Pending_add`) + `v_netact_dtls`.`Pending_add`)) AS `Total`,sum(`v_netact_dtls`.`Del`) AS `Deletions`,sum(`v_netact_dtls`.`Pending_del`) AS `Pending_del`,sum(`v_netact_dtls`.`Cum_Pending`) AS `Cum_Pending`,(sum(`v_netact_dtls`.`Del`) + sum(`v_netact_dtls`.`Pending_del`)) AS `NetSub`,sum(((((((`v_netact_dtls`.`Op_Active` + `v_netact_dtls`.`new`) + `v_netact_dtls`.`rec`) + `v_netact_dtls`.`Op_Pending_add`) + `v_netact_dtls`.`Pending_add`) - `v_netact_dtls`.`Del`) - `v_netact_dtls`.`Pending_del`)) AS `NetBal`,sum(`v_netact_dtls`.`Cl_Active`) AS `ClosingBal` from `v_netact_dtls` group by `v_netact_dtls`.`year_mon` order by `v_netact_dtls`.`month_number`;
CREATE OR REPLACE VIEW `v_netactpln_summary` AS select `net_activedtls_vw`.`year_mon` AS `Month`,`net_activedtls_vw`.`plan` AS `Plan`,`net_activedtls_vw`.`plan_id` AS `plan_id`,sum(`net_activedtls_vw`.`Op_Active`) AS `Op_Bal`,sum(`net_activedtls_vw`.`new`) AS `New`,sum(`net_activedtls_vw`.`rec`) AS `Reconnections`,sum(`net_activedtls_vw`.`ren`) AS `Renewals`,sum(`net_activedtls_vw`.`Op_Pending_add`) AS `Op_Pending_add`,sum(`net_activedtls_vw`.`Pending_add`) AS `Pending_add`,sum(((`net_activedtls_vw`.`new` + `net_activedtls_vw`.`rec`) + `net_activedtls_vw`.`ren`)) AS `NetAdditions`,sum(((`net_activedtls_vw`.`Op_Active` + `net_activedtls_vw`.`new`) + `net_activedtls_vw`.`rec`)) AS `Total`,sum(`net_activedtls_vw`.`Del`) AS `Deletions`,sum(`net_activedtls_vw`.`Pending_del`) AS `Pending_del`,sum(`net_activedtls_vw`.`Cum_Pending`) AS `Cum_Pending`,sum(`net_activedtls_vw`.`Del`) AS `NetSub`,sum((((((`net_activedtls_vw`.`Op_Active` + `net_activedtls_vw`.`new`) + `net_activedtls_vw`.`rec`) + `net_activedtls_vw`.`Op_Pending_add`) + `net_activedtls_vw`.`Pending_add`) - `net_activedtls_vw`.`Del`)) AS `NetBal`,sum(`net_activedtls_vw`.`Cl_Active`) AS `ClosingBal` from `net_activedtls_vw` group by `net_activedtls_vw`.`year_mon`,`net_activedtls_vw`.`plan` order by `net_activedtls_vw`.`month_number`,`net_activedtls_vw`.`plan_id` ;



insert ignore into m_code_value (id,code_id,code_value,order_position)
select null,c.id,'Halfyearly',0 from  m_code_value prm, m_code c where c.id=prm.code_id and c.code_name ='Bill Frequency';

insert ignore into m_code_value (id,code_id,code_value,order_position)
select null,c.id,' 	yearly',0 from  m_code_value prm, m_code c where c.id=prm.code_id and c.code_name ='Bill Frequency';

