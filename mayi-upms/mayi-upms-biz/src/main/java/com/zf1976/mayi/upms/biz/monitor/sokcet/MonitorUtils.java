/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.monitor.sokcet;

import com.zf1976.mayi.common.core.util.IpUtil;
import com.zf1976.mayi.upms.biz.monitor.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2021/1/1
 **/
public class MonitorUtils {

    private static final Logger Log = LoggerFactory.getLogger(MonitorUtils.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));


    public static SystemInfoVo getSystemInfo() {
        SystemInfoVo systemInfoVo = new SystemInfoVo();
        try {
            SystemInfo systemInfo = new SystemInfo();
            OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();

            systemInfoVo.setOperatingSystem(getOperatingSystem(operatingSystem));
            systemInfoVo.setCpu(getCpu(hardware.getProcessor()));
            systemInfoVo.setMemory(getMemory(hardware.getMemory()));
            systemInfoVo.setSwap(getSwap(hardware.getMemory()));
            systemInfoVo.setDisk(getDisk(operatingSystem));
            systemInfoVo.setDatetime(DATE_FORMAT_THREAD_LOCAL.get().format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(e.getMessage(), e.getCause());
        }finally {
            DATE_FORMAT_THREAD_LOCAL.remove();
        }
        return systemInfoVo;
    }

    /**
     * 获取交换分区信息
     *
     * @param globalMemory memory
     * @return /
     */
    private static SwapVO getSwap(GlobalMemory globalMemory) {
        SwapVO swapVO = new SwapVO();
        swapVO.setTotal(FormatUtil.formatBytes(globalMemory.getVirtualMemory().getSwapTotal()));
        swapVO.setAvailable(FormatUtil.formatBytes(globalMemory.getVirtualMemory().getSwapTotal() - globalMemory.getVirtualMemory().getSwapUsed()));
        swapVO.setUsed(FormatUtil.formatBytes(globalMemory.getVirtualMemory().getSwapUsed()));
        swapVO.setUsageRate(DECIMAL_FORMAT.format(globalMemory.getVirtualMemory().getSwapUsed()
                                                              / (double)globalMemory.getVirtualMemory().getSwapTotal() * 100));
        return swapVO;
    }

    /**
     *  获取磁盘信息
     *
     * @param operatingSystem ops
     * @return /
     */
    private static DiskVO getDisk(OperatingSystem operatingSystem) {
        List<OSFileStore> fileStores = null;
        try {
            fileStores = operatingSystem.getFileSystem()
                                        .getFileStores();
        } catch (Exception ignored) {}
        DiskVO diskVO = new DiskVO();
        if (fileStores != null) {
            for (OSFileStore fileStore : fileStores) {
                diskVO.setTotal(fileStore.getTotalSpace() > 0? FormatUtil.formatBytes(fileStore.getTotalSpace()) : "?");
                long used = fileStore.getTotalSpace() - fileStore.getUsableSpace();
                diskVO.setUsed(FormatUtil.formatBytes(used));
                diskVO.setUsageRate(DECIMAL_FORMAT.format(used / (double) fileStore.getTotalSpace() * 100));
                diskVO.setAvailable(FormatUtil.formatBytes(fileStore.getUsableSpace()));
            }
        }
        return diskVO;
    }

    /**
     * 获取内存信息
     *
     * @param globalMemory memory
     * @return /
     */
    private static MemoryVO getMemory(GlobalMemory globalMemory) {
        MemoryVO memoryVo = new MemoryVO();
        memoryVo.setTotal(FormatUtil.formatBytes(globalMemory.getTotal()));
        memoryVo.setAvailable(FormatUtil.formatBytes(globalMemory.getAvailable()));
        memoryVo.setUsed(FormatUtil.formatBytes(globalMemory.getTotal() - globalMemory.getAvailable()));
        memoryVo.setUsageRate(DECIMAL_FORMAT.format((globalMemory.getTotal() - globalMemory.getAvailable())
                                                                / (double) globalMemory.getTotal() * 100));
        return memoryVo;
    }

    /**
     * 获取cpu信息
     *
     * @param processor 中央处理器
     * @return /
     */
    private static CpuVO getCpu(CentralProcessor processor) {
        CpuVO cpuVo = new CpuVO();
        cpuVo.setName(processor.getProcessorIdentifier().getName());
        cpuVo.setPhysicalPackage(processor.getPhysicalPackageCount());
        cpuVo.setCore(processor.getPhysicalProcessorCount());
        cpuVo.setLogic(processor.getLogicalProcessorCount());
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 等待1秒...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long await = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softer = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + await + irq + softer + steal;
        String used = DECIMAL_FORMAT.format(100d * user / totalCpu + 100d * sys / totalCpu);
        String idled = DECIMAL_FORMAT.format(100d * idle / totalCpu);
        cpuVo.setUsed(used);
        cpuVo.setIdle(idled);
        return cpuVo;
    }

    /**
     * 获取操作系统信息
     *
     * @param operatingSystem 操作系统
     * @return /
     */
    private static OperatingSystemVO getOperatingSystem(OperatingSystem operatingSystem) {
        OperatingSystemVO operatingSystemVo = new OperatingSystemVO();
        operatingSystemVo.setOs(operatingSystem.toString());
        operatingSystemVo.setIp(IpUtil.getInterIP());
        operatingSystemVo.setRunningDay(FormatUtil.formatElapsedSecs(ManagementFactory.getRuntimeMXBean().getUptime()/1000));
        return operatingSystemVo;
    }
}
