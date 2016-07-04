$(function(){

    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var env = $("#env").val();
      var group = $("#currentGroup").val();
      if("Release" == theme){
        window.location.href = "release?env=" + env;
      } else if("Home" == theme) {
        window.location.href = "apilist?env=" + env;
      } else if("Config" == theme) {
        window.location.href = "createapi?env=" + env + "&group=" + group;
      } else if("LogView" == theme) {
        window.location.href = "pipelog?env=" + env;
      } else if("Users" == theme) {
       window.location.href = "user?env=" + env;
      } else if("Manual" == theme) {
        window.location.href = "manual?env=" + env;
      } else if("Cache" == theme) {
        window.location.href = "cachemanage?env=" + env;
      } else if("Instance" == theme) {
         window.location.href = "instancedetail?env=" + env;
      } else if("RecoverApi" == theme) {
        window.location.href = "recoverapi?env=" + env;
      }


    });

    // 跳转到帮助页面
    $(".help").on("click", function(e){
        e.preventDefault();
        var env = $("#env").val();
        location.href = "manual?env=" + env;
    });

    // 之前激活按钮的绑定事件
    $('a[data-toggle="tab"]').on('hide.bs.tab', function (e) {
      var theme = $(e.target).html(); // 之前激活按钮显示的关键字
    });

    $(".needTip").tooltip();

    // 按日期区间查询监控数据
    $("#statistic").on("click", function(){
        var startTime = $("#datetimepickerStart").val();
        var endTime = $("#datetimepickerEnd").val();
        if("" == startTime || "" == endTime) {
            return;
        }
        var group = $("#Group").val();
        var api = $("#api").val();
        var host = $("#host").val();
        startTime = startTime.replace(new RegExp("-","gm"),"/");
        // 当天零点
        var dateStart = new Date();
        var year = dateStart.getFullYear();
        var month = dateStart.getMonth();
        var date = dateStart.getDate();
        var tmp = new Date(year, month, date, 0, 0, 0);
        var zeroTime = Math.floor(new Date(year, month, date, 0, 0, 0).getTime()/1000);

        //明天零点
        var dateTo = parseInt(date) + 1;
        var zeroTimeTo = Math.floor(new Date(year, month, dateTo, 0, 0, 0).getTime()/1000);


        var startFlag = new Date(startTime);
        var UTCStartTime = startTime;
        startTime = Math.floor( new Date(startTime).getTime() / 1000); // 获取毫秒时间
        if (zeroTime > startTime) { // 查询的开始时间在当天凌晨之前，忽略0点之前的时间
            startTime = zeroTime;
            var monthSt = parseInt(month) + 1;
            var timeStr = year + "-" + monthSt + "-" + date + " " + "00:00";
            $("#datetimepickerStart").val(timeStr);

            $("#times").val(timeStr);

            $("#startYear").val(year);
            $("#startMonth").val(month);
            $("#startDate").val(date);
            $("#startHour").val("0");
            $("#startMinute").val("0");
        } else {
            $("#times").val(UTCStartTime);
            $("#startYear").val(year);
            $("#startMonth").val(month);
            $("#startDate").val(date);
            $("#startHour").val(startFlag.getHours());
            $("#startMinute").val(startFlag.getMinutes());
        }
        endTime = endTime.replace(new RegExp("-","gm"),"/");
        var dateEnd = new Date(endTime);
        endTime = Math.floor(new Date(endTime).getTime() / 1000);

        if(endTime > zeroTimeTo) {
            endTime = zeroTimeTo;
            var timeStrTo = year + "-" + monthSt + "-" + dateTo + " 00:00";
            $("#datetimepickerEnd").val(timeStrTo);
        }

        if(startTime > endTime) {
            return;
        }

        var hostName = $("#host").val();
        var apiName = "";
        var metric = new Array();
        metric.push("callCount");
        metric.push("maxResponseTime");
        metric.push("failure");
        var element = metric.join(",");
        $.post("querymonitor", {"stime":startTime, "etime":endTime, "metric":element, "group":group, "api":api, "host":host}, function (d) {
            parseStaticData(d, startTime, endTime);
        });
    });

    defaultDisplay();

    //页面加载时执行一次默认查询
    function defaultDisplay() {
        var hostName = $("#host").val();
        var apiName = $("#apiMonitor").val();

        // 当天零点
        var dateStart = new Date();
        var year = dateStart.getFullYear();
        var month = dateStart.getMonth();
        var date = dateStart.getDate();
        var hour = dateStart.getHours();
        var minutes = dateStart.getMinutes();
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        var tmp = new Date(year, month, date, 0, 0, 0);
        var startTime = Math.floor(new Date(year, month, date, 0, 0, 0).getTime()/1000);
        var endTime = Math.floor(new Date().getTime() / 1000); // 获取毫秒时间

        var monthSt = parseInt(month) + 1;
        var timeStr = year + "-" + monthSt + "-" + date + " " + "00:00";
        $("#datetimepickerStart").val(timeStr);

        var endStr = year + "-" + monthSt + "-" + date + " " + hour + ":" + minutes;
        $("#datetimepickerEnd").val(endStr);

        var metric = new Array();
        metric.push("callCount");
        metric.push("maxResponseTime");
        metric.push("failure");
        var element = metric.join(",");
        $.post("querymonitor", {"stime":startTime, "etime":endTime, "metric":element, "api":apiName}, function (d) {
            parseStaticData(d, startTime, endTime);
        });
    }

    // 处理结果
    function parseStaticData(d, startTime, endTime) {
        //d = eval('(' + d + ')');

        var callCountKeys = new Array();
        var callCountValues = new Array();
        var RTKeys = new Array();
        var RTValues = new Array();
        var failureKeys = new Array();
        var failureValues = new Array();
        for(var i=0; i<d.length; i++) { // 遍历返回的三个对象
            var metric = d[i].metric; // callCount, failure, maxResponseTime
            var dps = d[i].dps;
            var flag = $.isEmptyObject(dps); // dps是否为空对象
            if (flag == false) { // json非空
                for(var key in dps) {
                    //var key = key;
                    var value = dps[key];
                    if("callCount" == metric) { // 调用次数
                        callCountKeys.push(value.key);
                        callCountValues.push(value.value);
                    } else if("maxResponseTime" == metric) { // 最大返回时间
                        RTKeys.push(value.key);
                        RTValues.push(value.value);
                    } else if("failure" == metric) { // 失败次数
                        failureKeys.push(value.key);
                        failureValues.push(value.value);
                    }

                }
            } else {
                if("callCount" == metric) { // 调用次数

                } else if("maxResponseTime" == metric) { // 最大返回时间

                } else if("failure" == metric) { // 失败次数

                }

            }
        }

        // 开始组装callCount数据
        var callCountData = new Array();
        var len = callCountKeys.length;
        var start = startTime;
        var lastTimeCall = start;
        for(var i=0; i<len; i++) {
            var key = callCountKeys.shift();
            var value = callCountValues.shift();
             if(i == len-1) {
                   lastTimeCall = key;
             }
            var diff = key - start;
            diff = Math.floor(diff/60);
            for (var j=0; j< diff-1; j++) {
                callCountData.push(0);
            }
            callCountData.push(value);
            start = key;
        }

        var diff = endTime - lastTimeCall;
        diff = Math.floor(diff/60);
        for (var j=0; j< diff-1; j++) {
            callCountData.push(0);
        }

        // 开始组装RTCount的数据
        var RTCountData = new Array();
        var len = RTKeys.length;
        start = startTime;
        var lastTimeRT = start;
        for(var i=0; i<len; i++) {

            var key = RTKeys.shift();
            var value = RTValues.shift();
             if(i == len-1) {
                  lastTimeRT = key;
             }
            var diff = key - start;
            diff = Math.floor(diff/60);
            for(var j=0; j<diff-1; j++) {
                RTCountData.push(0);
            }
            RTCountData.push(value);
            start = key;
        }

        var diff = endTime - lastTimeRT;
        diff = Math.floor(diff/60);
        for (var j=0; j< diff-1; j++) {
            RTCountData.push(0);
        }

        // 开始组装failureCount的数据
        var failureCountData = new Array();
        var len = failureKeys.length;
        start = startTime;
        var lastTimeFail = start;
        for(var i=0; i<len; i++) {
            var key = failureKeys.shift();
            var value = failureValues.shift();
            if(i == len-1) {
                 lastTimeFail = key;
            }
            var diff = key - start;
            diff = Math.floor(diff/60);
            for(var j=0; j<diff-1; j++) {
                failureCountData.push(0);
            }
            failureCountData.push(value);
            start = key;
        }

        var diff = endTime - lastTimeFail;
        diff = Math.floor(diff/60);
        for (var j=0; j< diff-1; j++) {
            failureCountData.push(0);
        }


        var times = $("#times").val();
        var UTCTime = new Date(times);
        var yearUTC = UTCTime.getUTCFullYear();
        var monthUTC = UTCTime.getUTCMonth();
        var dayUTC = UTCTime.getUTCDate();
        var hourUTC = UTCTime.getUTCHours();
        var minuteUTC = UTCTime.getUTCMinutes();
        $("#callcountStatic").highcharts({
                 chart: {
                     zoomType: 'x'
                 },
                 title: {
                     text: '每分钟调用次数'
                 },
                 xAxis: {
                     type: 'datetime',
                     minRange:   1 * 3600000 // fourteen days
                 },
                 yAxis: {
                     title: {
                         text: 'call count'
                     }
                 },
                 legend: {
                     enabled: false
                 },
                 plotOptions: {
                     area: {
                         fillColor: {
                             linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                             stops: [
                                 [0, Highcharts.getOptions().colors[0]],
                                 [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                             ]
                         },
                         marker: {
                             radius: 2
                         },
                         lineWidth: 1,
                         states: {
                             hover: {
                                 lineWidth: 1
                             }
                         },
                         threshold: null
                     }
                 },

                 series: [{
                     type: 'area',
                     name: '调用次数',
                     pointInterval: 1 * 60 * 1000,
                     //pointStart: Date.UTC(2006, 0, 1),
                     pointStart: Date.UTC(yearUTC, monthUTC, dayUTC, hourUTC, minuteUTC),
                     //pointStart: new Date($("#startYear").val(), $("#startMonth").val(), $("#startDate").val(), $("#startHour").val(), $("#startMinute").val()),
                     data: callCountData
                 }],
               credits:{
                   text:"www.caitu99.com"
               }
             });

        $("#responsetimeStatic").highcharts({
              chart: {
                  zoomType: 'x'
              },
              title: {
                  text: '每分钟最大的RT时间'
              },
              xAxis: {
                  type: 'datetime',
                  minRange: 1 * 3600000 // fourteen days
              },
              yAxis: {
                  title: {
                      text: 'RT时间'
                  }
              },
              legend: {
                  enabled: false
              },
              plotOptions: {
                  area: {
                      fillColor: {
                          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                          stops: [
                              [0, Highcharts.getOptions().colors[0]],
                              [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                          ]
                      },
                      marker: {
                          radius: 2
                      },
                      lineWidth: 1,
                      states: {
                          hover: {
                              lineWidth: 1
                          }
                      },
                      threshold: null
                  }
              },

              series: [{
                  type: 'area',
                  name: 'RT时间',
                  pointInterval: 60 * 1000,
                  pointStart: Date.UTC(yearUTC, monthUTC, dayUTC, hourUTC, minuteUTC),
                  data: RTCountData
              }],
             credits:{
                 text:"www.caitu99.com"
             }
          });

          $("#failcountStatic").highcharts({
               chart: {
                   zoomType: 'x'
               },
               title: {
                   text: '每分钟错误次数'
               },
               xAxis: {
                   type: 'datetime',
                   minRange: 1 * 3600000 // fourteen days
               },
               yAxis: {
                   title: {
                       text: '错误次数'
                   }
               },
               legend: {
                   enabled: false
               },
               plotOptions: {
                   area: {
                       fillColor: {
                           linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                           stops: [
                               [0, Highcharts.getOptions().colors[0]],
                               [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                           ]
                       },
                       marker: {
                           radius: 2
                       },
                       lineWidth: 1,
                       states: {
                           hover: {
                               lineWidth: 1
                           }
                       },
                       threshold: null
                   }
               },

               series: [{
                   type: 'area',
                   name: '错误次数',
                   pointInterval: 1 * 60 * 1000,
                   pointStart: Date.UTC(yearUTC, monthUTC, dayUTC, hourUTC, minuteUTC),
                   data: failureCountData
               }],
              credits:{
                  text:"www.caitu99.com"
              }
           });


        }





    // 绑定highCharts跌得主题
    Highcharts.theme = {
    	colors: ["#2b908f", "#90ee7e", "#f45b5b", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
    		"#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
    	chart: {
    		backgroundColor: {
    			linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
    			stops: [
    				[0, '#2a2a2b'],
    				[1, '#3e3e40']
    			]
    		},
    		style: {
    			fontFamily: "'Unica One', sans-serif"
    		},
    		plotBorderColor: '#606063'
    	},
    	title: {
    		style: {
    			color: '#E0E0E3',
    			textTransform: 'uppercase',
    			fontSize: '20px'
    		}
    	},
    	subtitle: {
    		style: {
    			color: '#E0E0E3',
    			textTransform: 'uppercase'
    		}
    	},
    	xAxis: {
    		gridLineColor: '#707073',
    		labels: {
    			style: {
    				color: '#E0E0E3'
    			}
    		},
    		lineColor: '#707073',
    		minorGridLineColor: '#505053',
    		tickColor: '#707073',
    		title: {
    			style: {
    				color: '#A0A0A3'

    			}
    		}
    	},
    	yAxis: {
    		gridLineColor: '#707073',
    		labels: {
    			style: {
    				color: '#E0E0E3'
    			}
    		},
    		lineColor: '#707073',
    		minorGridLineColor: '#505053',
    		tickColor: '#707073',
    		tickWidth: 1,
    		title: {
    			style: {
    				color: '#A0A0A3'
    			}
    		}
    	},
    	tooltip: {
    		backgroundColor: 'rgba(0, 0, 0, 0.85)',
    		style: {
    			color: '#F0F0F0'
    		}
    	},
    	plotOptions: {
    		series: {
    			dataLabels: {
    				color: '#B0B0B3'
    			},
    			marker: {
    				lineColor: '#333'
    			}
    		},
    		boxplot: {
    			fillColor: '#505053'
    		},
    		candlestick: {
    			lineColor: 'white'
    		},
    		errorbar: {
    			color: 'white'
    		}
    	},
    	legend: {
    		itemStyle: {
    			color: '#E0E0E3'
    		},
    		itemHoverStyle: {
    			color: '#FFF'
    		},
    		itemHiddenStyle: {
    			color: '#606063'
    		}
    	},
    	credits: {
    		style: {
    			color: '#666'
    		}
    	},
    	labels: {
    		style: {
    			color: '#707073'
    		}
    	},

    	drilldown: {
    		activeAxisLabelStyle: {
    			color: '#F0F0F3'
    		},
    		activeDataLabelStyle: {
    			color: '#F0F0F3'
    		}
    	},

    	navigation: {
    		buttonOptions: {
    			symbolStroke: '#DDDDDD',
    			theme: {
    				fill: '#505053'
    			}
    		}
    	},

    	// scroll charts
    	rangeSelector: {
    		buttonTheme: {
    			fill: '#505053',
    			stroke: '#000000',
    			style: {
    				color: '#CCC'
    			},
    			states: {
    				hover: {
    					fill: '#707073',
    					stroke: '#000000',
    					style: {
    						color: 'white'
    					}
    				},
    				select: {
    					fill: '#000003',
    					stroke: '#000000',
    					style: {
    						color: 'white'
    					}
    				}
    			}
    		},
    		inputBoxBorderColor: '#505053',
    		inputStyle: {
    			backgroundColor: '#333',
    			color: 'silver'
    		},
    		labelStyle: {
    			color: 'silver'
    		}
    	},

    	navigator: {
    		handles: {
    			backgroundColor: '#666',
    			borderColor: '#AAA'
    		},
    		outlineColor: '#CCC',
    		maskFill: 'rgba(255,255,255,0.1)',
    		series: {
    			color: '#7798BF',
    			lineColor: '#A6C7ED'
    		},
    		xAxis: {
    			gridLineColor: '#505053'
    		}
    	},

    	scrollbar: {
    		barBackgroundColor: '#808083',
    		barBorderColor: '#808083',
    		buttonArrowColor: '#CCC',
    		buttonBackgroundColor: '#606063',
    		buttonBorderColor: '#606063',
    		rifleColor: '#FFF',
    		trackBackgroundColor: '#404043',
    		trackBorderColor: '#404043'
    	},

    	// special colors for some of the
    	legendBackgroundColor: 'rgba(0, 0, 0, 0.5)',
    	background2: '#505053',
    	dataLabelsColor: '#B0B0B3',
    	textColor: '#C0C0C0',
    	contrastTextColor: '#F0F0F3',
    	maskColor: 'rgba(255,255,255,0.3)'
    };

    // 每50秒查询一次
    setInterval(function() {
        var x = (new Date()).getTime(); // current time
        var etime = Math.floor(x/1000);
        var stime = etime - 10;
        var metric = new Array();
        metric.push("callCount");
        metric.push("maxResponseTime");
        metric.push("failure");
        var element = metric.join(",");
        //var api = "test";
        //var y = Math.random();

        $.post("querymonitor", {"stime":stime, "etime":etime, "metric":element}, function (d) {
            //d = eval('(' + d + ')');
            //var flag = $.isEmptyObject(d);
            var x = (new Date()).getTime(); // current time
            //d = eval('(' + d + ')');

            for(var i=0; i<d.length; i++) { // 遍历返回的三个对象
                var metric = d[i].metric; // callCount, failure, maxResponseTime
                var dps = d[i].dps;
                var flag = $.isEmptyObject(dps); // dps是否为空对象
                if (flag == false) { // json非空
                    for(var key in dps) {
                    	var value = dps[key];
                        if("callCount" == metric) { // 调用次数
                            $("#callCountKey").val(value.key);
                            $("#callCountValue").val(value.value);
                        } else if("maxResponseTime" == metric) { // 最大返回时间
                            $("#RTKey").val(value.key);
                            $("#RTValue").val(value.value);
                        } else if("failure" == metric) { // 失败次数
                            $("#failureKey").val(value.key);
                            $("#failureValue").val(value.value);
                        }

                    }
                } else {
                    if("callCount" == metric) { // 调用次数
                        $("#callCountKey").val(x);
                        $("#callCountValue").val(0);
                    } else if("maxResponseTime" == metric) { // 最大返回时间
                        $("#RTKey").val(x);
                        $("#RTValue").val(0);
                    } else if("failure" == metric) { // 失败次数
                        $("#failureKey").val(x);
                        $("#failureValue").val(0);
                    }

                }
            }
        }, "json");
    }, 10*1000); // 测试完毕删除100

    // Apply the theme
    Highcharts.setOptions(Highcharts.theme);

    // 调用次数图表展示
    Highcharts.setOptions({
                global: {
                    useUTC: false
                }
            });

//    $("#callcount").highcharts({
//        chart: {
//            type: 'spline',
//            animation: Highcharts.svg, // don't animate in old IE
//            marginRight: 10,
//            events: {
//                load: function () {
//
//                    // set up the updating of the chart each second
//                    var series = this.series[0];
//                    setInterval(function () {
//                        var x = (new Date()).getTime(); // current time
//                        var value = parseInt($("#callCountValue").val());
//                        series.addPoint([x, value], true, true);
//                    }, 60*1000);
//                }
//            }
//        },
//        title: {
//            text: '每分钟动态更新实时数据'
//        },
//        xAxis: {
//            type: 'datetime',
//            //tickPixelInterval: 150,
//
//        },
//        yAxis: {
//            title: {
//                text: 'Count'
//            },
//            plotLines: [{
//                value: 0,
//                width: 1,
//                color: '#808080'
//            }]
//        },
//        tooltip: {
//            formatter: function () {
//                return '<b>' + this.series.name + '</b><br/>' +
//                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
//                    Highcharts.numberFormat(this.y, 2);
//            }
//        },
//        legend: {
//            enabled: false
//        },
//        exporting: {
//            enabled: false
//        },
//        series: [{
//            name: 'call count',
//            pointInterval: 60 * 1000, // 一分钟
//            data: (function () {
//                // generate an array of random data
//                var data = [],
//                    time = (new Date()).getTime(),
//                    i;
//
//                for (i = -19; i <= 0; i += 1) {
//                    data.push({
//                        x: time + i * 60 * 1000,
//                        y: 0
//                    });
//                }
//                return data;
//            }())
//        }],
//        credits:{
//            text:"www.caitu99.com"
//        }
//    });

    $("#callcount").highcharts({
         chart: {
             type: 'spline',
             animation: Highcharts.svg, // don't animate in old IE
             marginRight: 10,
             events: {
                 load: function () {

                     // set up the updating of the chart each second
                     var series = this.series[0];
                     setInterval(function () {
//                             var x = (new Date()).getTime(), // current time
//                                 y = Math.random();

                    	 var key = $("#callCountKey").val();
                         var x = (new Date()).getTime(); // current time
                         var value = parseInt($("#callCountValue").val());
                         series.addPoint([x, value], true, true);
                     }, 10*1000);
                 }
             }
         },
         title: {
             text: '调用次数/分钟'
         },
         xAxis: {
             type: 'datetime',
             //tickPixelInterval: 150,

         },
         yAxis: {
             title: {
                 text: 'Count'
             },
             plotLines: [{
                 value: 0,
                 width: 1,
                 color: '#808080'
             }]
         },
         tooltip: {
             formatter: function () {
                 return '<b>' + this.series.name + '</b><br/>' +
                     Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                     Highcharts.numberFormat(this.y, 2);
             }
         },
         legend: {
             enabled: false
         },
         exporting: {
             enabled: false
         },
         series: [{
             name: 'call count',
             pointInterval: 10 * 1000, // 一分钟
             data: (function () {
                 // generate an array of random data
                 var data = [],
                     time = (new Date()).getTime(),
                     i;

                 for (i = -19; i <= 0; i += 1) {
                     data.push({
                         x: time + i * 10 * 1000,
                         y: 0
                     });
                 }
                 return data;
             }())
         }],
         credits:{
             text:"www.caitu99.com"
         }
     });

    $("#rt").highcharts({
         chart: {
             type: 'spline',
             animation: Highcharts.svg, // don't animate in old IE
             marginRight: 10,
             events: {
                 load: function () {

                     // set up the updating of the chart each second
                     var series = this.series[0];
                     setInterval(function () {
//                             var x = (new Date()).getTime(), // current time
//                                 y = Math.random();


                         var x = (new Date()).getTime(); // current time
                         var value = parseInt($("#RTValue").val());
                         series.addPoint([x, value], true, true);
                     }, 10*1000);
                 }
             }
         },
         title: {
             text: 'rt实时数据'
         },
         xAxis: {
             type: 'datetime',
             //tickPixelInterval: 150,

         },
         yAxis: {
             title: {
                 text: 'Count'
             },
             plotLines: [{
                 value: 0,
                 width: 1,
                 color: '#808080'
             }]
         },
         tooltip: {
             formatter: function () {
                 return '<b>' + this.series.name + '</b><br/>' +
                     Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                     Highcharts.numberFormat(this.y, 2);
             }
         },
         legend: {
             enabled: false
         },
         exporting: {
             enabled: false
         },
         series: [{
             name: 'RT',
             pointInterval: 10 * 1000, // 一分钟
             data: (function () {
                 // generate an array of random data
                 var data = [],
                     time = (new Date()).getTime(),
                     i;

                 for (i = -19; i <= 0; i += 1) {
                     data.push({
                         x: time + i * 10 * 1000,
                         y: 0
                     });
                 }
                 return data;
             }())
         }],
         credits:{
             text:"www.caitu99.com"
         }
     });

     $("#failcount").highcharts({
          chart: {
              type: 'spline',
              animation: Highcharts.svg, // don't animate in old IE
              marginRight: 10,
              events: {
                  load: function () {

                      // set up the updating of the chart each second
                      var series = this.series[0];
                      setInterval(function () {
//                              var x = (new Date()).getTime(), // current time
//                                  y = Math.random();
                          var x = (new Date()).getTime(); // current time
                          var value = parseInt($("#failureValue").val());
                          series.addPoint([x, value], true, true);
                      }, 10*1000);
                  }
              }
          },
          title: {
              text: '错误次数实时更新'
          },
          xAxis: {
              type: 'datetime',
              //tickPixelInterval: 150,

          },
          yAxis: {
              title: {
                  text: 'Count'
              },
              plotLines: [{
                  value: 0,
                  width: 1,
                  color: '#808080'
              }]
          },
          tooltip: {
              formatter: function () {
                  return '<b>' + this.series.name + '</b><br/>' +
                      Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                      Highcharts.numberFormat(this.y, 2);
              }
          },
          legend: {
              enabled: false
          },
          exporting: {
              enabled: false
          },
          series: [{
              name: 'fail count',
              pointInterval: 10 * 1000, // 一分钟
              data: (function () {
                  // generate an array of random data
                  var data = [],
                      time = (new Date()).getTime(),
                      i;

                  for (i = -19; i <= 0; i += 1) {
                      data.push({
                          x: time + i * 10 * 1000,
                          y: 0
                      });
                  }
                  return data;
              }())
          }],
          credits:{
              text:"www.caitu99.com"
          }
      });

    // Load the fonts
    //Highcharts.createElement('link', {
    //	href: 'http://fonts.googleapis.com/css?family=Unica+One',
    //	rel: 'stylesheet',
    //	type: 'text/css'
    //}, null, document.getElementsByTagName('head')[0]);



    $("#callcountStatic").highcharts({
         chart: {
             zoomType: 'x'
         },
         title: {
             text: '每分钟调用次数'
         },
         xAxis: {
             type: 'datetime',
             minRange: 14 * 24 * 3600000 // fourteen days
         },
         yAxis: {
             title: {
                 text: 'call count'
             }
         },
         legend: {
             enabled: false
         },
         plotOptions: {
             area: {
                 fillColor: {
                     linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                     stops: [
                         [0, Highcharts.getOptions().colors[0]],
                         [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                     ]
                 },
                 marker: {
                     radius: 2
                 },
                 lineWidth: 1,
                 states: {
                     hover: {
                         lineWidth: 1
                     }
                 },
                 threshold: null
             }
         },

         series: [{
             type: 'area',
             name: '调用次数',
             pointInterval: 1 * 60 * 1000,
             //pointStart: Date.UTC(2006, 0, 1),
             pointStart: Date.UTC($("#startYear").val(), $("#startMonth").val(), $("#startDate").val(), $("#startHour").val(), $("#startMinute").val()),
             data: [

             ]
         }],
       credits:{
           text:"www.caitu99.com"
       }
     });

     $("#responsetimeStatic").highcharts({
          chart: {
              zoomType: 'x'
          },
          title: {
              text: '每分钟最大的RT时间'
          },
          xAxis: {
              type: 'datetime',
              minRange: 1 * 3600000 // fourteen days
          },
          yAxis: {
              title: {
                  text: 'RT时间'
              }
          },
          legend: {
              enabled: false
          },
          plotOptions: {
              area: {
                  fillColor: {
                      linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                      stops: [
                          [0, Highcharts.getOptions().colors[0]],
                          [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                      ]
                  },
                  marker: {
                      radius: 2
                  },
                  lineWidth: 1,
                  states: {
                      hover: {
                          lineWidth: 1
                      }
                  },
                  threshold: null
              }
          },

          series: [{
              type: 'area',
              name: 'RT时间',
              pointInterval: 60 * 1000,
              pointStart: Date.UTC(2006, 0, 1),
              data: []
          }],
         credits:{
             text:"www.caitu99.com"
         }
      });

      $("#failcountStatic").highcharts({
           chart: {
               zoomType: 'x'
           },
           title: {
               text: '每分钟错误次数'
           },
           xAxis: {
               type: 'datetime',
               minRange: 1 * 3600000 // fourteen days
           },
           yAxis: {
               title: {
                   text: '错误次数'
               }
           },
           legend: {
               enabled: false
           },
           plotOptions: {
               area: {
                   fillColor: {
                       linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                       stops: [
                           [0, Highcharts.getOptions().colors[0]],
                           [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                       ]
                   },
                   marker: {
                       radius: 2
                   },
                   lineWidth: 1,
                   states: {
                       hover: {
                           lineWidth: 1
                       }
                   },
                   threshold: null
               }
           },

           series: [{
               type: 'area',
               name: '错误次数',
               pointInterval: 1 * 60 * 1000,
               pointStart: Date.UTC(2006, 0, 1),
               data: []
           }],
          credits:{
              text:"www.caitu99.com"
          }
       });

   $('#date-start').datetimepicker({ // 绑定时间选择器
        format: 'yyyy-mm-dd hh:ii',
        todayBtn: 1,
        todayHighlight: 1,
        weekStart: 1,
        startView: 2,
           forceParse: 0,
        pickerPosition: "bottom-left",
        //showMeridian: true,
        autoclose: true,
        //initialDate: "2015-01-01 12:12"
    }).on("changeDate", function(e){
           var currentDate = Math.floor(e.date.valueOf()/1000);
       });
    $('#date-end').datetimepicker({ // 绑定时间选择器
           format: 'yyyy-mm-dd hh:ii',
           todayBtn: 1,
           todayHighlight: 1,
           weekStart: 1,
           startView: 2,
           forceParse: 0,
           pickerPosition: "bottom-left",
           //showMeridian: true,
           autoclose: true
       }).on("changeDate", function(e){
           var currentDate = Math.floor(e.date.valueOf()/1000); //GMT时间，不符合北京时间

       });
})