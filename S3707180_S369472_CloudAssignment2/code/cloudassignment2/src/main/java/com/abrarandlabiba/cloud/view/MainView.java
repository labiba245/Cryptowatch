package com.abrarandlabiba.cloud.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.abrarandlabiba.cloud.ContactItem;
import com.abrarandlabiba.cloud.data.SMSRequest;
import com.abrarandlabiba.cloud.service.FileService;
import com.abrarandlabiba.cloud.service.LambdaService;
import com.abrarandlabiba.cloud.service.TableService;
import com.abrarandlabiba.cloud.service.TranslationService;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerStatistics;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Cursor;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.PlotOptionsBar;
import com.vaadin.flow.component.charts.model.RangeSelector;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Crypto Currency historical analysis Application",
        shortName = "Crypto Currency App",
        description = "Crypto Currency historical analysis Application using Binance data of last 24 hours.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
//@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")

public class MainView extends VerticalLayout {

	private static final long serialVersionUID = -3702687262495231414L;

	final Logger logger = LoggerFactory.getLogger(MainView.class);
	
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd 'T' HH:mm:ss");
    
    private static final int NUM_ELEMENTS = 5;
	
    private Chart topChart = new Chart(ChartType.BAR);
    private Chart topPie = new Chart(ChartType.PIE);
    private Chart bottomChart = new Chart(ChartType.BAR);
    private Grid<TickerStatistics> grid;
    private Label timeStamp_SP = new Label();
    Label btcPrice_LB = new Label();
    
    private TranslationService translationService;
    
    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
	public MainView(@Autowired TableService tableService, @Autowired FileService fileService,
			@Autowired LambdaService lambdaService, @Autowired TranslationService translationService) {

		this.translationService = translationService;
		// Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content"); 
        
        
        Span pageHeader_SP = new Span(); 
        pageHeader_SP.getElement().setProperty("innerHTML", "<H1> Crypto Currency 24 hours stats (Binance) </H1>");
        add(pageHeader_SP);
        
        HorizontalLayout topButtons_HL = new HorizontalLayout();
        add (topButtons_HL);
        final List <TickerStatistics> tickers = retrieveSortedTickers();
 
        Date date = new Date();

		Button refresh_BT = new Button("Refresh", e -> {
			tickers.clear(); 
			tickers.addAll(retrieveSortedTickers());
	        Date dateRefresh = new Date();
	        
			trickersChart(topChart, tickers, "Top 5 gaining", true);
			trickersChart(topPie, tickers, "Top 5 gaining", true);
			
	        List <TickerStatistics> tickersReverse = new ArrayList<>(tickers);
	        reverseSortTickers(tickersReverse);
	        
			trickersChart(bottomChart, tickersReverse, "Bottom 5 loosing", false);
			grid.setItems(tickers);
			
			timeStamp_SP.setText("Time Stamp: " + dateFormatter.format(dateRefresh));
			Notification.show("Refresh Complete");
			
	        double bitcoinPrice = findBitcoinPrice(tickers);
	        btcPrice_LB.setText("    Bitcoin Price: $" + ((bitcoinPrice) == -1 ? "unknown!" : String.format("%.2f", bitcoinPrice)));
		});
		refresh_BT.setIcon(new Icon(VaadinIcon.REFRESH));
        refresh_BT.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
     
        topButtons_HL.add(refresh_BT); 
		
		Button updateS3_BT = new Button("Store in S3", e -> {
			Notification.show(fileService.putCrypto(tickers));
		}) ;
		updateS3_BT.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		updateS3_BT.setIcon(new Icon(VaadinIcon.FILE));
		topButtons_HL.add(updateS3_BT);
        
        add (topButtons_HL);
        
		Button sendSMS_BT = new Button("Send SMS notifications", e -> {
			
			List<ContactItem> contacts = new ArrayList<>();
			tableService.loadContacts(contacts);
			
			List<SMSRequest> SMSRequests = createSMSList(tickers, contacts);
			lambdaService.callSMSFunction(SMSRequests);
			
//			temp_TA.setValue(Jackson.toJsonPrettyString(SMSRequests)); 
		}) ;
		sendSMS_BT.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		sendSMS_BT.setIcon(new Icon(VaadinIcon.MOBILE));
		topButtons_HL.add(sendSMS_BT);
        

		Button toAdmin_BT = new Button("To Admin Page >", e -> UI.getCurrent().getPage().setLocation("/admin"));
		topButtons_HL.add(toAdmin_BT);
		
		
        HorizontalLayout info_HL = new HorizontalLayout();
        timeStamp_SP.setText("Time Stamp: " + dateFormatter.format(date));
        info_HL.add(timeStamp_SP);
        
        double bitcoinPrice = findBitcoinPrice(tickers);
        btcPrice_LB.setText("Bitcoin Price: $" + ((bitcoinPrice) == -1 ? "unknown!" : String.format("%.2f", bitcoinPrice)));
        info_HL.add(btcPrice_LB);
        
        add(info_HL);

		//Top 5 horizontal charts
        HorizontalLayout topGain_HL = new HorizontalLayout();
		add(topGain_HL);
        trickersChart(topChart, tickers, "Top 5 Gaining", true);
        topGain_HL.add(topChart);

        trickersChart(topPie, tickers, "Top 5 Gaining", true);
        topGain_HL.add(topPie);
        
		//Spline for top item
		if (tickers.size() > 0) {
			List <Candlestick> candlesticks = retrieveTopTickerSticks(tickers.get(0).getSymbol());
			add(getChart(tickers.get(0).getSymbol(), candlesticks, true));
        } else {
			add(getChart("None", new ArrayList<Candlestick>(), true));
        }
        
		//Chart for bottom 5
        List <TickerStatistics> tickersReverse = new ArrayList<>(tickers);
        reverseSortTickers(tickersReverse);
     
		trickersChart(bottomChart, tickersReverse, "Bottom 5 Loosing", false);
        add(bottomChart);

        //Spline for bottom item
		if (tickersReverse.size() > 0) {
			List <Candlestick> candlesticks = retrieveTopTickerSticks(tickersReverse.get(0).getSymbol());
			add(getChart(tickersReverse.get(0).getSymbol(), candlesticks, false));
        } else {
			add(getChart("None", new ArrayList<Candlestick>(), false));
        }

        
		grid = newTickerGrid(tickers);
		add(grid);
	}

	private List<SMSRequest> createSMSList(List<TickerStatistics> tickers, List<ContactItem> contacts) {
		List<SMSRequest> sMSRequests = new ArrayList<>(); 

		String baseStr = "Top crypto currency gaining %s is %s. Bottom crypto currency loosing %s is %s";
		String englishStr = String.format(baseStr,
				"%" + tickers.get(0).getPriceChangePercent(), tickers.get(0).getSymbol(),
				"%" + tickers.get(tickers.size() - 1).getPriceChangePercent(),
				tickers.get(tickers.size() - 1).getSymbol());
		
		System.out.println("English Str:" + englishStr);	
		
		String arabicStr = String.format(translationService.translation(baseStr, "ar"), "%" + tickers.get(0).getPriceChangePercent(),
				tickers.get(0).getSymbol(), "%" + tickers.get(tickers.size() - 1).getPriceChangePercent(),
				tickers.get(tickers.size() - 1).getSymbol());
		
		System.out.println("Arabic Str:" + arabicStr);
		
		String chineseStr = String.format(translationService.translation(baseStr, "zh"),
				"%" + tickers.get(0).getPriceChangePercent(), tickers.get(0).getSymbol(),
				"%" + tickers.get(tickers.size() - 1).getPriceChangePercent(),
				tickers.get(tickers.size() - 1).getSymbol());

		System.out.println("chinese Str:" + chineseStr);

	
		for (ContactItem contact : contacts) {
			SMSRequest smsRequest = new SMSRequest();
			sMSRequests.add(smsRequest);
			smsRequest.setPhoneNumber(contact.getSMS());
			if ("Arabic".equals(contact.getLanguage())) {
				smsRequest.setMessage(arabicStr);
			} else if ("Chinese".equals(contact.getLanguage())) {
				smsRequest.setMessage(chineseStr);
			} else {
				smsRequest.setMessage(englishStr);
			}
		}		
		
		return sMSRequests;
	}

	private double findBitcoinPrice(final List<TickerStatistics> tickers) {
		double bitcoinPrice = -1;
		for (TickerStatistics ticker : tickers) {
        	if ("BTCUSDT".equals(ticker.getSymbol())) {
        		System.out.println("\n\n\n -------------- BTCUSDT: " + ticker.getWeightedAvgPrice());
        		bitcoinPrice = Double.parseDouble(ticker.getWeightedAvgPrice());
        	}        
        }
		return bitcoinPrice;
	}

	private void reverseSortTickers(List<TickerStatistics> tickersReverse) {
		tickersReverse.sort(new Comparator<TickerStatistics>() {
			@Override
			public int compare(TickerStatistics o1, TickerStatistics o2) {
				double d = Double.parseDouble(o1.getPriceChangePercent())
						- Double.parseDouble(o2.getPriceChangePercent());
				if (d > 0)
					return 1;
				if (d < 0)
					return -1;
				return 0;
			}
		});
	}

	private void sortTickers(List<TickerStatistics> tickers) {
		tickers.sort(new Comparator<TickerStatistics>() {
			@Override
			public int compare(TickerStatistics o1, TickerStatistics o2) {
				double d = Double.parseDouble(o1.getPriceChangePercent())
						- Double.parseDouble(o2.getPriceChangePercent());

				if (d > 0)
					return -1;
				if (d < 0)
					return 1;
				return 0;
			}
		});
	}

	private void trickersChart(Chart chart, List<TickerStatistics> tickers, String title, boolean isTop) {

        Configuration chartConf = chart.getConfiguration();

        chartConf.setTitle(title);

        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(1);
        chartConf.setTooltip(tooltip);

        PlotOptionsBar plotOptions = new PlotOptionsBar();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        
        chartConf.setPlotOptions(plotOptions);

        XAxis x = new XAxis();
        
        DataSeries series = new DataSeries();
        series.setName(title);
		
		for (int i = 0; i < 5 && i < tickers.size() ; i++) {
			double percentageChange = Double.parseDouble(tickers.get(i).getPriceChangePercent());
			
			DataSeriesItem tricker = new DataSeriesItem(tickers.get(i).getSymbol() + " " + percentageChange,
					Double.parseDouble(tickers.get(i).getPriceChangePercent()));
			
			if (i == 0) { // just for first item
				tricker.setSliced(true);
				tricker.setSelected(true);
			}
			
			series.add(tricker);
			x.addCategory(tickers.get(i).getSymbol());
		}
        chartConf.addxAxis(x);
		if (!isTop) {
			for (int i = 0; i < 5; ++i) {
				DataSeries ds = new DataSeries();
				ds.setVisible(false);
				ds.setName(" ");
				chartConf.addSeries(ds);
			}
		}
        chartConf.addSeries(series);
        chart.setVisibilityTogglingDisabled(true);

        chart.setWidth("30em");
	}

	
    protected Component getChart(String symbol, List<Candlestick> sticksList, boolean isTop) {
        Chart chart = new Chart(ChartType.SPLINE);
        chart.setTimeline(true);

        Configuration chartConf = chart.getConfiguration();
        chartConf.getTitle().setText("" + symbol + " Price Change");
        chartConf.getTooltip().setEnabled(true);
        
        DataSeries dataSeries = new DataSeries();

		dataSeries.setId("id:"+symbol);
		dataSeries.setName(symbol);
		
		for (Candlestick cs : sticksList) {
			DataSeriesItem item = new DataSeriesItem();
			item.setX(cs.getOpenTime());
			item.setY(Double.parseDouble(cs.getOpen()));
			dataSeries.add(item);
		}
		
		if (!isTop) {
			for (int i = 0; i < 5; ++i) {
				DataSeries ds = new DataSeries();
				ds.setVisible(false);
				ds.setName(" ");
				chartConf.addSeries(ds);
			}
		}
		chartConf.addSeries(dataSeries);

        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(1);
        chartConf.setRangeSelector(rangeSelector);

        return chart;
    }
	
		
	
	
	private Grid<TickerStatistics> newTickerGrid(List<TickerStatistics> tickers) {
		Grid<TickerStatistics> grid = new Grid<>();
		grid.setItems(tickers);
		grid.addColumn(TickerStatistics::getSymbol).setHeader("Symbol");
		grid.addColumn(TickerStatistics::getPriceChangePercent).setHeader("Price Change Percent");
		grid.addColumn(TickerStatistics::getWeightedAvgPrice).setHeader("Weighted Avg Price");
		grid.addColumn(TickerStatistics::getVolume).setHeader("Volume");
		grid.setWidth("50em");
		return grid;
	}

  
    private List <TickerStatistics> retrieveSortedTickers() {
    	BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
    	BinanceApiRestClient client = factory.newRestClient();
    	List <TickerStatistics> allTickers = client.getAll24HrPriceStatistics();
    	if (allTickers==null) {
    		allTickers = new ArrayList <>(); 
    	}
    	
		sortTickers(allTickers);
	
    	return allTickers;
    }
    
    private List <Candlestick> retrieveTopTickerSticks(String symbol) {
    	BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
    	BinanceApiRestClient client = factory.newRestClient();
    	List <Candlestick> allcandleSticks = client.getCandlestickBars(symbol, CandlestickInterval.DAILY, 20, null, null);
    	
    	if (allcandleSticks==null) {
    		allcandleSticks = new ArrayList <>(); 
    	}
    	
    	return allcandleSticks;
    }
}
