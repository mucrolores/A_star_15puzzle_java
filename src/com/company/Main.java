package com.company;

import javafx.css.Size;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*Test data
0 1 2 3 4 6 7 11 8 5 13 10 12 9 14 15 // 10 Step pass 0.027s
1 5 3 7 0 6 2 11 4 13 9 14 8 12 10 15 // 16 Step pass 0.041s
0 3 8 6 14 5 9 7 13 4 11 10 12 2 1 15 // 40 Step pass 20s
9 4 0 6 2 8 3 13 5 1 11 10 12 7 14 15 // 40 Step pass 310s
4 3 7 6 1 14 5 10 12 11 9 2 8 0 13 15
6 12 4 7 10 3 1 2 5 9 14 13 0 8 11 15

*/
class GlobalData
{
    public static int N = 4;
    public static int Size = 16;
    public static int Ratio = 1;
}

class State{

    private int GValue;
    private int HValue;
    private int FValue;
    private int[] Data = new int[GlobalData.Size];
    private int SpaceIndex;
    public State Parent;

    public int GetGValue() { return GValue; }
    public int GetHValue() { return HValue; }
    public int GetFValue() { return FValue; }
    public int GetSpaceIndex() { return SpaceIndex; }
    public int [] GetData(){
        int[] ToReturn = new int[GlobalData.Size];
        System.arraycopy(Data,0,ToReturn,0,GlobalData.Size);
        return ToReturn;
    }

    public void SetGValue(int InputG) { GValue = InputG; }
    public void SetHValue(int InputH) { HValue = InputH; }
    public void SetFValue(int InputF) { FValue = InputF; }
    public void SetSpaceIndex(int InputSpace) { SpaceIndex = InputSpace; }
    public void SetData(int [] InputData){ if (GlobalData.Size >= 0) System.arraycopy(InputData, 0, Data, 0, GlobalData.Size); }

    public void CalculateHValue(){
        int TmpH = 0;
        for(int i=0;i<GlobalData.Size;i++)
        {
            if(i!=GlobalData.Size-1)
            {
                TmpH += Math.abs( Data[i]/GlobalData.N - i/GlobalData.N) + Math.abs( Data[i]%GlobalData.N - i%GlobalData.N);
            }
        }
        HValue = GlobalData.Ratio * TmpH;
    }
    public void CalculateFValue(){
        FValue = GValue + HValue;
    }

    public String GetDataInString(){
        StringBuilder tmp = new StringBuilder();
        for(int i=0;i<15;i++)
        {
            tmp.append(Data[i]).append(",");
        }
        tmp.append(Data[15]);
        return tmp.toString();
    }

    public void PrintState(){
        for(int i=0;i<GlobalData.Size;i++)
        {
            if(Data[i] < 10) { System.out.print(" "); }
            if(Data[i] != GlobalData.Size-1)
            {
                System.out.print(Data[i] + " ");
            }
            else
            {
                System.out.print("   ");
            }
            if( (i+1) % GlobalData.N == 0)
            {
                System.out.print("\n");
            }
        }
        System.out.println("G : " + GValue);
        System.out.println("H : " + HValue);
        System.out.println("F : " + FValue);
    }
}

public class Main {

    static long Counter = 0;


    public static void A_Star(State Start)
    {
        ArrayList<String> OPENList = new ArrayList<>();

        HashMap<String,State> OPENListHashMap = new HashMap<>();
        HashMap<String,State> CLOSEListHashMap = new HashMap<>();

        int [] CurrentData;
        int tmpHValue;
        State Current;
        Current = Start;
        int SmallestIndex;
        OPENList.add(Current.GetDataInString());
        OPENListHashMap.put(Current.GetDataInString(),Current);

        while(!OPENList.isEmpty())
        {
            SmallestIndex = 0;
            for(int i=0;i<OPENList.size();i++)
            {
                if((OPENListHashMap.get(OPENList.get(i)).GetFValue() < OPENListHashMap.get(OPENList.get(SmallestIndex)).GetFValue()) ||
                        (OPENListHashMap.get(OPENList.get(i)).GetFValue() == OPENListHashMap.get(OPENList.get(SmallestIndex)).GetFValue() &&
                                OPENListHashMap.get(OPENList.get(i)).GetGValue() > OPENListHashMap.get(OPENList.get(SmallestIndex)).GetGValue()))
                {
                    SmallestIndex = i;
                }
            }
            Counter++;

            Current = OPENListHashMap.get(OPENList.get(SmallestIndex));
            OPENListHashMap.remove(Current.GetDataInString());
            OPENList.remove(SmallestIndex);
            if(Current.GetHValue() == 0)
            {
                break;
            }
            CLOSEListHashMap.put(Current.GetDataInString(),Current);
            CurrentData = Current.GetData();
            //System.out.println("Current G:"+Current.GetGValue()+", "+"H:"+Current.GetHValue() + ", " + "F:" + Current.GetFValue());

            if(Counter%100 == 0)
            {
                System.out.println("Current G:"+Current.GetGValue()+", "+"H:"+Current.GetHValue() + ", " + "F:" + Current.GetFValue());
            }

            /* Expand 4 Dir for State */
            if(Current.GetSpaceIndex()/GlobalData.N < GlobalData.N-1 ) // swipe up state, dir = 0
            {
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()+GlobalData.N);
                State SUState = new State();
                SUState.SetData(CurrentData);
                SUState.SetSpaceIndex(Current.GetSpaceIndex()+GlobalData.N);
                SUState.SetGValue(Current.GetGValue()+1);
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()+GlobalData.N);
                //calculate the next step's HValue
                tmpHValue = Current.GetHValue();
                tmpHValue -= GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()+GlobalData.N]/GlobalData.N - (Current.GetSpaceIndex()+GlobalData.N)/GlobalData.N) );
                tmpHValue += GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()+GlobalData.N]/GlobalData.N - (Current.GetSpaceIndex())/GlobalData.N) );
                SUState.SetHValue(tmpHValue);
                SUState.CalculateFValue();
                //Add this state to OPENList;
                if(!CLOSEListHashMap.containsKey(SUState.GetDataInString()))
                {
                    State ToFindState = OPENListHashMap.get(SUState.GetDataInString());
                    if(ToFindState != null)
                    {
                        if(ToFindState.GetFValue() > SUState.GetFValue() ||
                                (ToFindState.GetFValue() == SUState.GetFValue() && ToFindState.GetHValue() > SUState.GetHValue()))
                        {
                            OPENListHashMap.remove(SUState.GetDataInString());
                            OPENListHashMap.put(SUState.GetDataInString(),SUState);
                        }
                    }
                    else
                    {
                        OPENList.add(SUState.GetDataInString());
                        OPENListHashMap.put(SUState.GetDataInString(),SUState);
                    }
                }
            }
            if(Current.GetSpaceIndex()/GlobalData.N > 0) // swipe down state, dir = 1
            {
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()-GlobalData.N);
                State SDState = new State();
                SDState.SetData(CurrentData);
                SDState.SetSpaceIndex(Current.GetSpaceIndex()-GlobalData.N);
                SDState.SetGValue(Current.GetGValue()+1);
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()-GlobalData.N);
                //calculate the next step's HValue
                tmpHValue = Current.GetHValue();
                tmpHValue -= GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()-GlobalData.N]/GlobalData.N - (Current.GetSpaceIndex()-GlobalData.N)/GlobalData.N) );
                tmpHValue += GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()-GlobalData.N]/GlobalData.N - (Current.GetSpaceIndex())/GlobalData.N) );
                SDState.SetHValue(tmpHValue);
                SDState.CalculateFValue();
                //Add this state to OPENList;
                if(!CLOSEListHashMap.containsKey(SDState.GetDataInString()))
                {
                    State ToFindState = OPENListHashMap.get(SDState.GetDataInString());
                    if(ToFindState != null)
                    {
                        if(ToFindState.GetFValue() > SDState.GetFValue() ||
                                (ToFindState.GetFValue() == SDState.GetFValue() && ToFindState.GetHValue() > SDState.GetHValue()))
                        {
                            OPENListHashMap.remove(SDState.GetDataInString());
                            OPENListHashMap.put(SDState.GetDataInString(),SDState);
                        }
                    }
                    else
                    {
                        OPENList.add(SDState.GetDataInString());
                        OPENListHashMap.put(SDState.GetDataInString(),SDState);
                    }
                }
            }
            if(Current.GetSpaceIndex()%GlobalData.N < GlobalData.N-1) // swipe left state, dir = 2
            {
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()+1);
                State SLState = new State();
                SLState.SetData(CurrentData);
                SLState.SetSpaceIndex(Current.GetSpaceIndex()+1);
                SLState.SetGValue(Current.GetGValue()+1);
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()+1);
                //calculate the next step's HValue
                tmpHValue = Current.GetHValue();
                tmpHValue -= GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()+1]%GlobalData.N - (Current.GetSpaceIndex()+1)%GlobalData.N) );
                tmpHValue += GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()+1]%GlobalData.N - (Current.GetSpaceIndex())%GlobalData.N) );
                SLState.SetHValue(tmpHValue);
                SLState.CalculateFValue();
                //Add this state to OPENList;
                if(!CLOSEListHashMap.containsKey(SLState.GetDataInString()))
                {
                    State ToFindState = OPENListHashMap.get(SLState.GetDataInString());
                    if(ToFindState != null)
                    {
                        if(ToFindState.GetFValue() > SLState.GetFValue() ||
                                (ToFindState.GetFValue() == SLState.GetFValue() && ToFindState.GetHValue() > SLState.GetHValue()))
                        {
                            OPENListHashMap.remove(SLState.GetDataInString());
                            OPENListHashMap.put(SLState.GetDataInString(),SLState);
                        }
                    }
                    else
                    {
                        OPENList.add(SLState.GetDataInString());
                        OPENListHashMap.put(SLState.GetDataInString(),SLState);
                    }
                }
            }
            if(Current.GetSpaceIndex()%GlobalData.N > 0) // swipe right state, dir = 3
            {
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()-1);
                State SRState = new State();
                SRState.SetData(CurrentData);
                SRState.SetSpaceIndex(Current.GetSpaceIndex()-1);
                SRState.SetGValue(Current.GetGValue()+1);
                SwapData(CurrentData,Current.GetSpaceIndex(),Current.GetSpaceIndex()-1);
                //calculate the next step's HValue
                tmpHValue = Current.GetHValue();
                tmpHValue -= GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()-1]%GlobalData.N - (Current.GetSpaceIndex()-1)%GlobalData.N) );
                tmpHValue += GlobalData.Ratio*( Math.abs(CurrentData[Current.GetSpaceIndex()-1]%GlobalData.N - (Current.GetSpaceIndex())%GlobalData.N) );
                SRState.SetHValue(tmpHValue);
                SRState.CalculateFValue();
                //Add this state to OPENList;
                if(!CLOSEListHashMap.containsKey(SRState.GetDataInString()))
                {
                    State ToFindState = OPENListHashMap.get(SRState.GetDataInString());
                    if(ToFindState != null)
                    {
                        if(ToFindState.GetFValue() > SRState.GetFValue() ||
                                (ToFindState.GetFValue() == SRState.GetFValue() && ToFindState.GetHValue() > SRState.GetHValue()))
                        {
                            OPENListHashMap.remove(SRState.GetDataInString());
                            OPENListHashMap.put(SRState.GetDataInString(),SRState);
                        }
                    }
                    else
                    {
                        OPENList.add(SRState.GetDataInString());
                        OPENListHashMap.put(SRState.GetDataInString(),SRState);
                    }
                }
            }
        }
        System.out.println("Finish !!!");
        Current.PrintState();
    }

    public static void SwapData(int[] Data, int index1, int index2)
    {
        int tmp = Data[index1];
        Data[index1] = Data[index2];
        Data[index2] = tmp;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        long StartTime,EndTime;

        int [] TmpData = new int[GlobalData.Size];
        for(int i=0;i<GlobalData.Size;i++)
        {
            TmpData[i] = scanner.nextInt();
        }
        State StartState = new State();
        StartState.SetData(TmpData);
        StartState.SetGValue(0);
        StartState.CalculateHValue();
        StartState.CalculateFValue();
        StartState.SetSpaceIndex(15);
        StartTime = System.nanoTime();
        A_Star(StartState);
        EndTime = System.nanoTime();

        System.out.println("StartTime : " + StartTime + ", EndTime : " + EndTime);
        System.out.println("Time cost : "+ (double)(EndTime-StartTime)/1_000_000_000);
    }
}