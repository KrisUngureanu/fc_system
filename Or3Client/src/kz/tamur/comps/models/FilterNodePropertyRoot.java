package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class FilterNodePropertyRoot extends PropertyNode {
    public FilterNodePropertyRoot() {
        super(null, "Root", -1, null, false, null);
        PropertyNode pn = new PropertyNode(this, "title", Types.STRING, null, false, null);
        PropertyReestr.registerFilterProperty(pn);
        PropertyReestr.registerProperty(pn);
        new PropertyNode(this, "inversFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        EnumValue[] evs = {
            new EnumValue(Constants.UNION_OR, "по или"),
            new EnumValue(Constants.UNION_AND, "по и")
        };
        new PropertyNode(this, "unionFlr", Types.ENUM, evs, false, new Integer(Constants.UNION_OR));
        pn = new PropertyNode(this, "attrFlr", Types.REF, null , false,null);
        new PropertyNode(this, "linkFlr", Types.KRNOBJECT_ITEM, null , false,null);
        PropertyReestr.registerFilterProperty(pn);
        EnumValue[] oper = {
            new EnumValue(Constants.OPER_ZERO, ""),
            new EnumValue(Constants.OPER_EQ, Constants.OP_EQ),
            new EnumValue(Constants.OPER_NEQ, Constants.OP_NEQ),
            new EnumValue(Constants.OPER_GT, Constants.OP_GT),
            new EnumValue(Constants.OPER_LT, Constants.OP_LT),
            new EnumValue(Constants.OPER_GEQ, Constants.OP_GEQ),
            new EnumValue(Constants.OPER_LEQ, Constants.OP_LEQ),
            new EnumValue(Constants.OPER_EXIST,Constants.OP_EXIST),
            new EnumValue(Constants.OPER_NOT_EXIST, Constants.OP_NOT_EXIST),
            new EnumValue(Constants.OPER_INCLUDE, Constants.OP_INCLUDE),
            new EnumValue(Constants.OPER_EXCLUDE, Constants.OP_EXCLUDE),
            new EnumValue(Constants.OPER_CONTAIN, Constants.OP_CONTAIN),
            new EnumValue(Constants.OPER_START_WITH, Constants.OP_START_WITH),
            new EnumValue(Constants.OPER_ANOTHER, Constants.OP_ANOTHER),
            new EnumValue(Constants.OPER_ASCEND, Constants.OP_ASCEND),
            new EnumValue(Constants.OPER_DESCEND, Constants.OP_DESCEND),
            new EnumValue(Constants.OPER_FINISH_ON, Constants.OP_FINISH_ON),
            new EnumValue(Constants.OPER_NOT_CONTAIN, Constants.OP_NOT_CONTAIN)
        };
        new PropertyNode(this, "operFlr", Types.ENUM, oper, false, new Integer(Constants.OPER_ZERO));
        EnumValue[] evs1 = {
            new EnumValue(Constants.COMPARE_VALUE, "значение атрибута"),
            new EnumValue(Constants.COMPARE_FUNC, "функция"),
            new EnumValue(Constants.COMPARE_ATTR, "атрибут"),
            new EnumValue(Constants.SQL_PAR, "параметр")
        };
        new PropertyNode(this, "compFlr", Types.ENUM, evs1, false, new Integer(Constants.COMPARE_VALUE));
		PropertyNode valFlr = new PropertyNode(this, "valFlr", -1, null, false, null);
        new PropertyNode(valFlr, "krnObjFlr", Types.KRNOBJECT, null , false,null);
        pn = new PropertyNode(valFlr, "exprFlr", Types.EXPR, null , false,null);
        PropertyReestr.registerFilterProperty(pn);
        pn = new PropertyNode(this, "linkPar", Types.STRING, null , false,null);
        PropertyReestr.registerFilterProperty(pn);
        EnumValue[] likeEscapes = {
                new EnumValue(Constants.ESCAPE_NOT, "отсутствует"),
                new EnumValue(Constants.ESCAPE_MANUAL, "пользовательская"),
                new EnumValue(Constants.ESCAPE_DEFAULT, "по умолчанию")
            };
        new PropertyNode(this, "likeEscape", Types.ENUM, likeEscapes, false, new Integer(Constants.ESCAPE_NOT));
        PropertyNode lang = new PropertyNode(this, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "name");
        new PropertyNode(this, "groupFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        EnumValue[] grpFunc = {
                new EnumValue(Constants.GROUP_ZERO, ""),
                new EnumValue(Constants.GROUP_COUNT, Constants.GR_COUNT),
                new EnumValue(Constants.GROUP_SUM, Constants.GR_SUM),
                new EnumValue(Constants.GROUP_MAX, Constants.GR_MAX),
                new EnumValue(Constants.GROUP_MIN, Constants.GR_MIN),
                new EnumValue(Constants.GROUP_AVG, Constants.GR_AVG)
            };
        new PropertyNode(this, "grpFuncFlr", Types.ENUM, grpFunc, false, new Integer(Constants.GROUP_ZERO));
        new PropertyNode(this, "independFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(this, "maxIndFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(this, "relativeFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        EnumValue[] kolOper = {
            new EnumValue(Constants.OPER_ZERO, ""),
            new EnumValue(Constants.OPER_EQ, Constants.OP_EQ),
            new EnumValue(Constants.OPER_NEQ, Constants.OP_NEQ),
            new EnumValue(Constants.OPER_GT, Constants.OP_GT),
            new EnumValue(Constants.OPER_LT, Constants.OP_LT),
            new EnumValue(Constants.OPER_GEQ, Constants.OP_GEQ),
            new EnumValue(Constants.OPER_LEQ, Constants.OP_LEQ)
        };
        new PropertyNode(this, "kolOperFlr", Types.ENUM, kolOper, false, new Integer(Constants.OPER_ZERO));
        pn = new PropertyNode(this, "kolObjFlr", Types.STRING, null , false,null);
        PropertyReestr.registerFilterProperty(pn);
        EnumValue[] evs2 = {
            new EnumValue(Constants.TRANS_ZERO, "нулевая"),
            new EnumValue(Constants.TRANS_ALL, "все"),
            new EnumValue(Constants.TRANS_CUR, "текущая")
        };
        new PropertyNode(this, "transFlr", Types.ENUM, evs2, false, new Integer(Constants.TRANS_ZERO));
        new PropertyNode(this, "maxTrFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(this, "mandatoryFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(this, "excludeFlr", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(this, "children", Types.COMPONENT, null, false, null);
        new PropertyNode(this, "joinCls", Types.BOOLEAN, null, false, Boolean.FALSE);
        pn = new PropertyNode(this, "attrParent", Types.REF, null , false,null);
        PropertyReestr.registerFilterProperty(pn);
        EnumValue[] operJoin = {
            new EnumValue(Constants.OPER_ZERO, ""),
            new EnumValue(Constants.OPER_EQ, Constants.OP_EQ),
            new EnumValue(Constants.OPER_NEQ, Constants.OP_NEQ),
            new EnumValue(Constants.OPER_GT, Constants.OP_GT),
            new EnumValue(Constants.OPER_LT, Constants.OP_LT),
            new EnumValue(Constants.OPER_GEQ, Constants.OP_GEQ),
            new EnumValue(Constants.OPER_LEQ, Constants.OP_LEQ)
        };
        new PropertyNode(this, "operJoin", Types.ENUM, operJoin, false, new Integer(Constants.OPER_ZERO));
        pn = new PropertyNode(this, "attrChild", Types.REF, null , false,null);
        PropertyReestr.registerFilterProperty(pn);
        pn = new PropertyNode(this, "comment", Types.STRING, null, false, null);
        PropertyReestr.registerFilterProperty(pn);
        new PropertyNode(this, "respReg", Types.BOOLEAN,null,false,null);
        new PropertyNode(this, "isNodeMenu", Types.BOOLEAN,null,false,false);
   }
}