#!/usr/bin/env python3
from decimal import Decimal

def exact_compare(actual,operator,expected):
    a=Decimal(actual); e=Decimal(expected); c=(a>e)-(a<e)
    return {'>=':c>=0,'<=':c<=0,'==':c==0,'>':c>0,'<':c<0}[operator]

def feature_expected(value):
    value=value.strip().lower()
    if value in {'true','supported','yes','1'}: return True
    if value in {'false','unsupported','no','0'}: return False
    return None

def dependency_absence(status):
    return {'available':'NOT_ENUMERATED','incomplete':'UNKNOWN','unavailable':'UNKNOWN','not_applicable':'NOT_APPLICABLE','unknown':'UNKNOWN'}.get(status,'UNKNOWN')

def technical_needed(tab): return tab in {8,9}

assert exact_compare('9007199254740993','==','9007199254740993')
assert not exact_compare('9007199254740993','==','9007199254740992')
assert exact_compare('18446744073709551615','>','9007199254740993')
assert feature_expected('banana') is None
assert feature_expected('false') is False and feature_expected('supported') is True
assert dependency_absence('available')=='NOT_ENUMERATED'
assert dependency_absence('incomplete')=='UNKNOWN'
assert dependency_absence('unavailable')=='UNKNOWN'
assert dependency_absence('not_applicable')=='NOT_APPLICABLE'
assert [technical_needed(i) for i in range(15)] == [False,False,False,False,False,False,False,False,True,True,False,False,False,False,False]
print('PASS 1.0.1 exact-evidence and lazy-analysis state model')
