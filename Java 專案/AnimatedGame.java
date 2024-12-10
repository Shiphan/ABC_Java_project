import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;


public class AnimatedGame {
    
    // 動作判定
    private boolean isDefenseActive = false;
    private boolean isGameOver = false;
    private int level = 1;
    private final int maxLevel = 3;

    // 主角數值
    private int heroAttackPower = 30;
    private int heroHealth = 100;
    private int heroMaxHealth = 100;
    private int heroAttackSpeed = 3000; // 毫秒
    private int heroMana = 0;
    private final int heroMaxMana = 100;

    // 怪物數值
    private int monsterAttackPower = 5;
    private int monsterHealth = 50;
    private int monsterUltimatePower = 35;
    private int monsterAttackSpeed = 5000; // 毫秒
    private int monsterUltimateInterval = 20000; // 毫秒

    // 人像圖案
        private ImageIcon heroPortrait, monsterPortrait,GGPortrait ,DEFPortrait ,AttackPortrait,GGmonsterPortrait;

        public AnimatedGame() {
            showStartMenu();
        }

    // 遊戲視窗
    private JFrame frame;
    private JLabel heroLabel, monsterLabel, heroStatsLabel, monsterStatsLabel, manaLabel, ultimateWarningLabel;
    private JButton[] magicCards = new JButton[4];
    private Timer heroAttackTimer, monsterAttackTimer, manaRegenTimer, monsterUltimateTimer;
    private JProgressBar heroHealthBar, monsterHealthBar;

    // 顯示開始選單
    private void showStartMenu() {
        frame = new JFrame("Java 遊戲專案");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to the dungeon", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        frame.add(titleLabel, BorderLayout.NORTH);

        JButton startButton = new JButton("Game Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.addActionListener(e -> {
            frame.dispose();
            setupGameWindow();
            startTimers();
        });

        frame.add(startButton, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // 初始化遊戲視窗
    private void setupGameWindow() {
        frame = new JFrame("Java 遊戲專案");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        // 主角血量進度條
        heroHealthBar = new JProgressBar(0, heroMaxHealth); // 設定最大值
        heroHealthBar.setValue(heroHealth); // 設定初始值
        heroHealthBar.setBounds(50, 100, 250, 20); // 設定位置與大小
        heroHealthBar.setStringPainted(true); // 顯示文字
        heroHealthBar.setForeground(Color.GREEN); // 設定顏色
        frame.add(heroHealthBar);

        // 怪物血量進度條
        monsterHealthBar = new JProgressBar(0, monsterHealth); // 設定最大值
        monsterHealthBar.setValue(monsterHealth); // 設定初始值
        monsterHealthBar.setBounds(450, 100, 250, 20); // 設定位置與大小
        monsterHealthBar.setStringPainted(true); // 顯示文字
        monsterHealthBar.setForeground(Color.RED); // 設定顏色
        frame.add(monsterHealthBar);

        

        // 載入人像
        heroPortrait = new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\hero.png");
        monsterPortrait = new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\monster.png"); 
        GGPortrait = new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\GGgirl.png"); 
        DEFPortrait = new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\DEF.png"); 
        AttackPortrait = new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\attack.png"); 
        GGmonsterPortrait = new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\GGmonster.png"); 

        // 主角人像
        heroLabel = new JLabel(heroPortrait);
        heroLabel.setBounds(50, 130, 250, 250);

        // 怪物人像
        monsterLabel = new JLabel(monsterPortrait);
        monsterLabel.setBounds(450, 100, 300, 350);

        frame.add(heroLabel);
        frame.add(monsterLabel);

        // 數值顯示
        heroStatsLabel = new JLabel("主角 - 血量: " + heroHealth + "/" + heroMaxHealth + ", 攻擊力: " + heroAttackPower);
        heroStatsLabel.setBounds(10, 10, 300, 20);

        monsterStatsLabel = new JLabel("怪物 - 血量: " + monsterHealth + ", 攻擊力: " + monsterAttackPower);
        monsterStatsLabel.setBounds(500, 10, 300, 20);

        manaLabel = new JLabel("魔力值: " + heroMana + "/" + heroMaxMana);
        manaLabel.setBounds(10, 40, 300, 20);

        frame.add(heroStatsLabel);
        frame.add(monsterStatsLabel);
        frame.add(manaLabel);

        // 大招提示
        ultimateWarningLabel = new JLabel("", SwingConstants.CENTER);
        ultimateWarningLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ultimateWarningLabel.setForeground(Color.RED);
        ultimateWarningLabel.setBounds(200, 100, 400, 50);
        frame.add(ultimateWarningLabel);

        // 魔法卡區域
        // 调整按钮面板
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        cardPanel.setBounds(90, 440, 600, 100); 
        
        for (int i = 0; i < 4; i++) {
            magicCards[i] = new JButton();
            magicCards[i].setPreferredSize(new Dimension(200, 40)); 
            magicCards[i].addActionListener(new MagicCardAction());
            cardPanel.add(magicCards[i]); 
        }

        frame.add(cardPanel);
        frame.setVisible(true);
    }

    // 啟動各項定時器
    private void startTimers() {
        // 主角自動攻擊怪物
        heroAttackTimer = new Timer(heroAttackSpeed, e -> attackMonster());
        heroAttackTimer.start();

        // 怪物自動攻擊主角
        monsterAttackTimer = new Timer(monsterAttackSpeed, e -> attackHero());
        monsterAttackTimer.start();

        // 怪物大招攻擊
        monsterUltimateTimer = new Timer(monsterUltimateInterval, e -> monsterUltimateAttack());
        monsterUltimateTimer.start();

        // 魔力值恢復
        manaRegenTimer = new Timer(1000, e -> regenerateMana());
        manaRegenTimer.start();

        // 初始生成魔法卡
        generateMagicCards();
    }

    // 移動
    private void moveHero(int deltaX) {
        Point position = heroLabel.getLocation();
        heroLabel.setLocation(position.x + deltaX, position.y);
    }
    private void moveMonster(int deltaX) {
        Point position = monsterLabel.getLocation();
        monsterLabel.setLocation(position.x + deltaX, position.y);
    }

    // 主角攻擊怪物
    private void attackMonster() {
        if (monsterHealth > 0 && !isGameOver) {
            // 動畫效果：更換圖片與移動
            SwingUtilities.invokeLater(() -> {
                heroLabel.setIcon(AttackPortrait); // 更換為攻擊圖片
                moveHero(150); // 向前移動
            });
    
            // 短暫延遲後恢復圖片與位置
            Timer resetTimer = new Timer(500, e -> {
                heroLabel.setIcon(heroPortrait); // 恢復靜止圖片
                moveHero(-150); // 移回原位
                // 扣除怪物血量
                monsterHealth -= heroAttackPower;
                updateLabels();
                if (monsterHealth <= 0) {
                    SwingUtilities.invokeLater(() -> {
                    monsterLabel.setIcon(GGmonsterPortrait); 
                    moveMonster(30); // 向前移動
                    });
                    if (level < maxLevel) {
                        nextLevel();
                    } else {
                        gameEnd("恭喜！你通關了所有關卡！");
                    }
                }
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }

    // 怪物攻擊主角
    private void attackHero() {
        if (isDefenseActive) {
            if (heroHealth <= 0) {
                gameEnd("怪物勝利！");
            }else{
            JOptionPane.showMessageDialog(frame, "防禦成功！主角免疫本次傷害！");
            isDefenseActive = false;
            }
        } else if (heroHealth > 0 && !isGameOver) {
            moveMonster(-80); // 向前移動
            Timer resetTimer = new Timer(500, e -> {
                monsterLabel.setIcon(monsterPortrait); // 恢復靜止圖片
                moveMonster(80); // 移回原位
                // 扣除主角血量
                heroHealth -= monsterAttackPower;
                updateLabels();
                if (heroHealth <= 0) {
                    SwingUtilities.invokeLater(() -> {
                        heroLabel.setIcon(GGPortrait); 
                        moveHero(30); // 向前移動
                    });
                    gameEnd("怪物勝利！");
                }
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
            
        }
    }

    // 怪物大招攻擊
    private void monsterUltimateAttack() {
        new Thread(() -> {
            for (int i = 3; i > 0; i--) {
                ultimateWarningLabel.setText("The monster ult attack: " + i + " sec");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
            ultimateWarningLabel.setText("");
            if (heroHealth > 0 && !isGameOver) {
                heroHealth -= monsterUltimatePower;
                updateLabels();
                if (heroHealth <= 0) {
                    SwingUtilities.invokeLater(() -> {
                        heroLabel.setIcon(GGPortrait); 
                        moveHero(30); // 向前移動
                    });
                    gameEnd("怪物勝利！");
                }
            }
        }).start();
    }

    // 魔力值恢復
    private void regenerateMana() {
        if (heroMana < heroMaxMana) {
            heroMana++;
            manaLabel.setText("魔力值: " + heroMana + "/" + heroMaxMana);
        }
    }
   
    private void nextLevel() {
        if (level >= maxLevel) {
            gameEnd("恭喜！你通關了所有關卡！");
            return;
        }
    
        // 更新關卡相關數據
        level++;
        monsterHealth = 50 + level * 10; // 每關怪物血量增加
        monsterAttackPower += 2; // 每關攻擊力增加
        monsterAttackSpeed = Math.max(2000, monsterAttackSpeed - 200); // 攻擊速度加快
        monsterUltimatePower += 5; // 大招傷害增加
    
        // 停止計時器，等待玩家按確定後啟動下一關
        if (heroAttackTimer != null) heroAttackTimer.stop();
        if (monsterAttackTimer != null) monsterAttackTimer.stop();
        if (monsterUltimateTimer != null) monsterUltimateTimer.stop();
        if (manaRegenTimer != null) manaRegenTimer.stop();
    
        // 顯示訊息，等待按下確認後繼續
        JOptionPane.showMessageDialog(frame, "恭喜！進入第 " + level + " 關！");
        
        // 更新怪物圖片
        if (level == 2) {
            monsterLabel.setIcon(new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\monster_level2.png"));
        } else if (level == 3) {
            monsterLabel.setIcon(new ImageIcon("ABC_and_Shiphan_git\\Java 專案\\picture\\monster_level3.png"));
        }
    
        // 重置怪物進度條和狀態
        monsterHealthBar.setMaximum(monsterHealth);
        monsterHealthBar.setValue(monsterHealth);
    
        // 重新啟動計時器
        startTimers();
        updateLabels();
    }
    
    private void updateHealthBarColors() {
        // 主角血量進度條顏色
        int heroHealthPercent = (int) ((double) heroHealth / heroMaxHealth * 100);
        heroHealthBar.setForeground(heroHealthPercent > 50 ? Color.GREEN : (heroHealthPercent > 20 ? Color.ORANGE : Color.RED));
    
        // 怪物血量進度條顏色
        int monsterHealthPercent = (int) ((double) monsterHealth / (50 + level * 20) * 100); // 根據等級調整最大值
        monsterHealthBar.setForeground(monsterHealthPercent > 50 ? Color.RED : (monsterHealthPercent > 20 ? Color.ORANGE : Color.DARK_GRAY));
    }

    // 更新數值顯示
    private void updateLabels() {
        heroStatsLabel.setText("主角 - 血量: " + heroHealth + "/" + heroMaxHealth + ", 攻擊力: " + heroAttackPower);
        monsterStatsLabel.setText("怪物 - 血量: " + monsterHealth + ", 攻擊力: " + monsterAttackPower);
        manaLabel.setText("魔力值: " + heroMana + "/" + heroMaxMana);

        // 更新進度條
        heroHealthBar.setMaximum(heroMaxHealth); // 更新主角最大血量
        heroHealthBar.setValue(heroHealth); // 更新主角當前血量
        monsterHealthBar.setValue(monsterHealth); // 更新怪物當前血量

        updateHealthBarColors();
    }

    // 遊戲結束
    private void gameEnd(String message) {
        if (isGameOver) return; 
        isGameOver = true;

        if (heroAttackTimer != null) heroAttackTimer.stop();
        if (monsterAttackTimer != null) monsterAttackTimer.stop();
        if (manaRegenTimer != null) manaRegenTimer.stop();
        if (monsterUltimateTimer != null) monsterUltimateTimer.stop();
        JOptionPane.showMessageDialog(frame, message);
        System.exit(1);
    }

    // 隨機生成魔法卡
    private void generateMagicCards() {
        String[] cardNames = {"血量最大值 +5 (消耗10魔力)", "攻擊力 +1 (消耗10魔力)", "防禦 (消耗10魔力)", "血量 +20 (消耗10魔力)"};
        for (JButton button : magicCards) {
            String card = cardNames[new Random().nextInt(cardNames.length)];
            button.setText(card);
        }
    }

    // 魔法卡點擊事件
    private class MagicCardAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String card = source.getText();
    
            if (heroMana >= 10) {
                heroMana -= 10;
                if (card.contains("血量最大值 +5")) {
                    heroMaxHealth += 5;
                } else if (card.contains("攻擊力 +1")) {
                    heroAttackPower += 1;
                } else if (card.contains("防禦")) {
                    isDefenseActive = true;
                    SwingUtilities.invokeLater(() -> {
                        heroLabel.setIcon(DEFPortrait);
                        moveHero(30); // 向前移動
                    });
                } else if (card.contains("血量 +20")) {
                    heroHealth = Math.min(heroMaxHealth, heroHealth + 20);
                }
                generateMagicCards();
                updateLabels();
            } else {
                JOptionPane.showMessageDialog(frame, "魔力值不足！");
            }
        }
    }
    

    public static void main(String[] args) {
        new AnimatedGame();
    }
}
