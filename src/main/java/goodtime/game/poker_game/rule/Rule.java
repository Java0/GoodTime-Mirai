package goodtime.game.poker_game.rule;



import goodtime.game.poker_game.Poker;
import goodtime.game.poker_game.poker_game_util.PokerUtil;

import java.util.ArrayList;
import java.util.HashMap;


//规则类，露露哇露露哒！

public class Rule {

    private String rule;

    private int power;


    public Rule(ArrayList<Poker> outPokers) {
        ArrayList<Poker> noRepeatPokers = PokerUtil.getNoRepeatPokers(outPokers);
        HashMap<Poker, Integer> pokersCountMap = PokerUtil.getPokersCountMap(outPokers);
        switch (outPokers.size()) {
            case 1:
                rule = Rules.A;
                power = outPokers.get(0).getPower();
                break;
            case 2:
                if (isAA(noRepeatPokers)) {
                    rule = Rules.AA;
                    power = noRepeatPokers.get(0).getPower() * 2;
                } else if (isSuperBomb(noRepeatPokers)) {
                    rule = Rules.SUPER_BOMB;
                    power = Integer.MAX_VALUE;
                }
                break;
            case 3:
                if (isAAA(noRepeatPokers)) {
                    rule = Rules.AAA;
                    power = noRepeatPokers.get(0).getPower() * 3;
                }
                break;
            case 4:
                if (isBomb(noRepeatPokers)) {
                    rule = Rules.BOMB;
                    power = noRepeatPokers.get(0).getPower() * 4;
                } else if (isAAAB(noRepeatPokers, pokersCountMap)) {
                    rule = Rules.AAAB;
                    power = getThreeCountPoker(noRepeatPokers, pokersCountMap).getPower() * 3;
                }
                break;
            case 5:
                if (isAAABB(noRepeatPokers, pokersCountMap)) {
                    rule = Rules.AAABB;
                    power = getThreeCountPoker(noRepeatPokers, pokersCountMap).getPower() * 3;
                } else if (isABC(noRepeatPokers, outPokers)) {
                    rule = Rules.ABC;
                    power = getContinuousPower(noRepeatPokers);
                    continuousCount = noRepeatPokers.size();
                }
                break;
            default:
                if (isABC(noRepeatPokers, outPokers)) {
                    rule = Rules.ABC;
                    power = getContinuousPower(noRepeatPokers);
                    continuousCount = noRepeatPokers.size();
                } else if (isAABBCC(noRepeatPokers, pokersCountMap)) {
                    rule = Rules.AABBCC;
                    power = getContinuousPower(noRepeatPokers) * 2;
                    continuousCount = noRepeatPokers.size();
                } else if (isAAAABB(noRepeatPokers, pokersCountMap)) {
                    rule = Rules.AAAABB;
                    power = getFourCountPoker(noRepeatPokers, pokersCountMap).getPower() * 4;
                } else if (isSpecialPlane(noRepeatPokers, pokersCountMap)) {
                    rule = Rules.SPECIAL_PLANE;
                } else if (isPlane(noRepeatPokers, pokersCountMap) || isPlane(noRepeatPokers, pokersCountMap, outPokers)) {
                    rule = Rules.PLANE;
                }
                break;
        }
    }

    private boolean isAA(ArrayList<Poker> noRepeatPokers) {
        return noRepeatPokers.size() == 1;
    }

    private boolean isSuperBomb(ArrayList<Poker> noRepeatPokers) {
        return noRepeatPokers.size() == 2 && noRepeatPokers.get(0).getMark() == '鬼' && noRepeatPokers.get(1).getMark() == '王';
    }

    private boolean isAAA(ArrayList<Poker> noRepeatPokers) {
        return noRepeatPokers.size() == 1;
    }

    private boolean isBomb(ArrayList<Poker> noRepeatPokers) {
        return noRepeatPokers.size() == 1;
    }

    private Poker getThreeCountPoker(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {
        for (Poker poker : noRepeatPokers) {
            if (pokersCountMap.get(poker) == 3) {
                return poker;
            }
        }
        return null;
    }


    private boolean isAAAB(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {
        return noRepeatPokers.size() == 2 && getThreeCountPoker(noRepeatPokers, pokersCountMap) != null;

    }

    private boolean isAAABB(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {
        return noRepeatPokers.size() == 2 && getThreeCountPoker(noRepeatPokers, pokersCountMap) != null;
    }

    private int continuousCount;

    public int getContinuousCount() {
        return continuousCount;
    }

    //判断是否连牌,并获取连牌的相加值
    private int getContinuousPower(ArrayList<Poker> noRepeatPokers) {

        int temp = 0;

        for (int i = 1; i < noRepeatPokers.size(); i++) {
            if (noRepeatPokers.get(i).getPower() - noRepeatPokers.get(i - 1).getPower() != 1) {
                return 0;
            } else {
                temp += noRepeatPokers.get(i).getPower();
            }
        }
        return temp + noRepeatPokers.get(0).getPower();
    }

    private boolean haveUselessPokers(ArrayList<Poker> noRepeatPokers) {
        return noRepeatPokers.contains(new Poker('2', 14)) || noRepeatPokers.contains(new Poker('鬼', 15)) || noRepeatPokers.contains(new Poker('王', 16));
    }

    //顺子
    private boolean isABC(ArrayList<Poker> noRepeatPokers, ArrayList<Poker> outPokers) {

        return !haveUselessPokers(noRepeatPokers) && noRepeatPokers.size() >= 5 && noRepeatPokers.size() == outPokers.size() && getContinuousPower(noRepeatPokers) != 0;

    }

    //连对
    private boolean isAABBCC(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {
        if (noRepeatPokers.size() >= 3) {

            if (haveUselessPokers(noRepeatPokers)) {
                return false;
            }

            for (Poker poker : noRepeatPokers) {
                if (pokersCountMap.get(poker) != 2) {
                    return false;
                }
            }
        }

        return getContinuousPower(noRepeatPokers) != 0;
    }

    private Poker getFourCountPoker(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {
        for (Poker poker : noRepeatPokers) {
            if (pokersCountMap.get(poker) == 4) {
                return poker;
            }
        }
        return null;
    }


    private boolean isAAAABB(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {

        Poker temp = getFourCountPoker(noRepeatPokers, pokersCountMap);
        if (noRepeatPokers.size() == 2 && temp != null) {
            noRepeatPokers.remove(temp);
            return pokersCountMap.get(noRepeatPokers.get(0)) == 2;
        }
        return false;
    }


    private int wingLength;

    public int getWingLength() {
        return wingLength;
    }

    private boolean isSpecialPlane(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {
        ArrayList<Poker> body = new ArrayList<>();
        ArrayList<Poker> wings = new ArrayList<>();

        for (Poker poker : noRepeatPokers) {
            if (pokersCountMap.get(poker) == 3) {
                body.add(poker);
            } else {
                wings.add(poker);
            }
        }

        this.power = getContinuousPower(body);

        if (wings.isEmpty()) {
            if (body.size() == 4 && this.power != 0) {
                this.continuousCount = body.size();
                body.remove(body.size() - 1);
                this.power = getContinuousPower(body);
                return true;
            }
        }
        return false;


    }

    private boolean isPlane(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap) {

        ArrayList<Poker> body = new ArrayList<>();
        ArrayList<Poker> wings = new ArrayList<>();

        boolean specialMode = false;

        for (Poker poker : noRepeatPokers) {
            if (pokersCountMap.get(poker) == 3) {
                body.add(poker);
            } else if (pokersCountMap.get(poker) == 4) {
                specialMode = true;
                for (int i = 0; i < 2; i++) {
                    wings.add(poker);
                }
            } else {
                wings.add(poker);
            }
        }

        int wingLength = 0;

        this.power = getContinuousPower(body);

        if (!specialMode) {

            if (wings.isEmpty()) {
                if (this.power != 0) {
                    this.continuousCount = body.size();
                    return true;
                }
            } else {
                wingLength = pokersCountMap.get(wings.get(0));
                for (Poker wing : wings) {
                    if (pokersCountMap.get(wing) != wingLength) {
                        return false;
                    }
                }
            }

        } else {

            wingLength = 2;
            for (Poker wing : wings) {
                if (pokersCountMap.get(wing) != 4 && pokersCountMap.get(wing) != wingLength) {
                    return false;
                }
            }

        }

        this.continuousCount = body.size();

        this.wingLength = wingLength;

        return (body.size() == wings.size());

    }

    private boolean isPlane(ArrayList<Poker> noRepeatPokers, HashMap<Poker, Integer> pokersCountMap, ArrayList<Poker> outPokers) {

        ArrayList<Poker> body = new ArrayList<>();
        ArrayList<Poker> wings = (ArrayList<Poker>) outPokers.clone();

        for (Poker poker : noRepeatPokers) {
            if (pokersCountMap.get(poker) == 3) {
                body.add(poker);
                for (int i = 0; i < 3; i++) {
                    wings.remove(poker);
                }
            }
        }

        this.power = getContinuousPower(body);

        int wingLength = pokersCountMap.get(wings.get(0));

        if (this.power != 0) {
            for (Poker wing : wings) {
                if (pokersCountMap.get(wing) != wingLength) {
                    return false;
                }
            }
        } else {
            return false;
        }

        this.continuousCount = body.size();

        this.wingLength = wingLength;

        return body.size() == PokerUtil.getNoRepeatPokers(wings).size();
    }

    private boolean basicConditions(Rule rule) {
        return rule.getRule().equals(this.rule) && rule.getPower() > this.power;
    }

    public boolean comparison(Rule rule) {

        boolean basicConditions = basicConditions(rule);

        switch (rule.getRule()) {
            case Rules.A:
            case Rules.AA:
            case Rules.AAA:
            case Rules.AAAB:
            case Rules.AAABB:
            case Rules.AAAABB:
                return basicConditions;
            case Rules.ABC:
            case Rules.AABBCC:
                return basicConditions && this.continuousCount == rule.getContinuousCount();
            case Rules.SUPER_BOMB:
                return true;
            case Rules.BOMB:
                if (!this.rule.equals("炸弹")) {
                    return true;
                } else {
                    return basicConditions;
                }

            case Rules.SPECIAL_PLANE:
                return rule.getPower() > this.power
                        && (this.getRule().equals("特殊飞机") || this.getRule().equals("飞机"))
                        && ((this.getContinuousCount() == 4 && this.getWingLength() == 0) || (this.getContinuousCount() == 3 && this.getWingLength() == 1));

            case Rules.PLANE:
                return basicConditions && this.continuousCount == rule.getContinuousCount() && this.wingLength == rule.getWingLength();
        }
        return false;
    }

    public String getRule() {
        return rule;
    }

    public int getPower() {
        return power;
    }
}

